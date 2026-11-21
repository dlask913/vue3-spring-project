import assert from 'node:assert/strict';
import { setImmediate } from 'node:timers/promises';
import test from 'node:test';
import axios, { AxiosError } from 'axios';
import { installAuthInterceptors } from '../src/api/authInterceptors.js';

const deferred = () => {
  let resolve;
  const promise = new Promise(done => {
    resolve = done;
  });
  return { promise, resolve };
};

const response = (config, data) => ({
  config,
  data,
  status: 200,
  statusText: 'OK',
  headers: {},
});
const httpError = (config, status = 401, errorCode = 'TOKEN_EXPIRED') =>
  new AxiosError('요청 실패', 'ERR_BAD_RESPONSE', config, null, {
    config,
    status,
    data: { errorCode },
    headers: {},
  });

// 실제 Axios 요청, 응답 인터셉터를 실행하고 네트워크 전송만 adapter로 대체
function createHarness({ handleApi, handleRefresh } = {}) {
  const calls = { api: [], refresh: [], saves: 0, clears: 0, routes: [] };
  const refreshStarted = deferred();
  const userStore = {
    userId: 1,
    username: 'admin',
    accessToken: 'old-token',
    email: 'admin@example.com',
    userRole: 'ROLE_ADMIN',
    setAuthInfo(userId, username, accessToken, email, userRole) {
      calls.saves++;
      Object.assign(this, { userId, username, accessToken, email, userRole });
    },
    clearAuthInfo() {
      calls.clears++;
      Object.assign(this, {
        userId: null,
        username: null,
        accessToken: null,
        email: null,
        userRole: null,
      });
    },
  };
  const api = axios.create({
    baseURL: 'http://test.invalid',
    adapter: async config => {
      calls.api.push({
        url: config.url,
        authorization: config.headers.Authorization,
        retry: config._retry,
      });
      if (handleApi) return handleApi(config);
      if (!config._retry) throw httpError(config);
      return response(config, 'ok');
    },
  });
  const refreshClient = axios.create({
    baseURL: api.defaults.baseURL,
    timeout: 10000,
    adapter: async config => {
      calls.refresh.push(config);
      refreshStarted.resolve();
      return handleRefresh
        ? handleRefresh(config)
        : response(config, 'new-token');
    },
  });
  installAuthInterceptors(api, {
    refreshClient,
    userStore,
    router: { push: path => calls.routes.push(path) },
  });
  return { api, calls, userStore, refreshStarted };
}

test('동시 만료 요청은 갱신 한 번을 공유하고 새 토큰으로 각각 한 번 재시도한다', async () => {
  const gate = deferred();
  const h = createHarness({
    handleRefresh: async config => {
      await gate.promise;
      return response(config, 'new-token');
    },
  });
  const pending = Promise.all(['/a', '/b', '/c'].map(url => h.api.get(url)));
  await h.refreshStarted.promise;
  await setImmediate();
  assert.equal(h.calls.refresh.length, 1);
  assert.equal(h.calls.api.length, 3);
  gate.resolve();
  assert.equal((await pending).length, 3);
  assert.equal(h.calls.saves, 1);
  assert.equal(h.calls.api.length, 6);
  assert.ok(
    h.calls.api
      .filter(call => call.retry)
      .every(call => call.authorization === 'Bearer new-token'),
  );
  assert.equal(h.calls.refresh[0].url, '/refresh');
  assert.equal(h.calls.refresh[0].withCredentials, true);
  assert.equal(h.calls.refresh[0].timeout, 10000);
  assert.equal(h.calls.refresh[0].headers.Authorization, undefined);
});

test('갱신 후 늦게 도착한 이전 토큰의 만료 응답은 추가 갱신 없이 재시도한다', async () => {
  const late = deferred();
  const h = createHarness({
    handleApi: async config => {
      if (config._retry) return response(config, 'ok');
      if (config.url === '/late') await late.promise;
      throw httpError(config);
    },
  });
  const slow = h.api.get('/late');
  await h.api.get('/fast');
  late.resolve();
  await slow;
  assert.equal(h.calls.refresh.length, 1);
  assert.equal(h.calls.api.length, 4);
});

test('재시도도 만료 오류를 반환하면 추가 갱신이나 세 번째 요청 없이 종료한다', async () => {
  const h = createHarness({
    handleApi: config => {
      throw httpError(config);
    },
  });
  await assert.rejects(
    h.api.get('/always-expired'),
    error => error.response.status === 401,
  );
  assert.equal(h.calls.refresh.length, 1);
  assert.equal(h.calls.api.length, 2);
});

test('서명·형식 오류와 비밀번호 오류에서는 토큰 갱신을 시도하지 않는다', async () => {
  for (const code of [
    'TOKEN_INVALID_SIGNATURE',
    'TOKEN_MALFORMED',
    'PASSWORD_MISMATCH',
  ]) {
    const h = createHarness({
      handleApi: config => {
        throw httpError(config, 401, code);
      },
    });
    await assert.rejects(h.api.get('/invalid'));
    assert.equal(h.calls.refresh.length, 0);
    assert.equal(h.calls.api.length, 1);
  }
});

test('갱신 인증 실패 시 모든 대기 요청을 거부하고 로그인 초기화·이동은 한 번 수행한다', async () => {
  const gate = deferred();
  const h = createHarness({
    handleRefresh: async config => {
      await gate.promise;
      throw httpError(config, 401, 'REFRESH_TOKEN_INVALID');
    },
  });
  const pending = Promise.allSettled(
    ['/a', '/b', '/c'].map(url => h.api.get(url)),
  );
  await h.refreshStarted.promise;
  await setImmediate();
  gate.resolve();
  const results = await pending;
  assert.ok(
    results.every(
      result =>
        result.status === 'rejected' &&
        result.reason.response.data.errorCode === 'REFRESH_TOKEN_INVALID',
    ),
  );
  assert.equal(h.calls.refresh.length, 1);
  assert.equal(h.calls.api.length, 3);
  assert.equal(h.calls.clears, 1);
  assert.deepEqual(h.calls.routes, ['/login']);
});

test('갱신 통신 실패 후 공유 상태가 해제되어 다음 요청에서 다시 갱신할 수 있다', async () => {
  let fail = true;
  const h = createHarness({
    handleRefresh: config => {
      if (fail) throw new AxiosError('시간 초과', 'ECONNABORTED', config);
      return response(config, 'new-token');
    },
  });
  await assert.rejects(
    h.api.get('/first'),
    error => error.code === 'ECONNABORTED',
  );
  assert.equal(h.calls.clears, 0);
  assert.equal(h.userStore.accessToken, 'old-token');
  fail = false;
  await h.api.get('/next');
  assert.equal(h.calls.refresh.length, 2);
  assert.equal(h.userStore.accessToken, 'new-token');
});

test('성공한 갱신 상태도 해제되어 다음 만료 시 새 갱신을 수행한다', async () => {
  let generation = 0;
  const h = createHarness({
    handleRefresh: config => response(config, `new-token-${++generation}`),
  });
  await h.api.get('/first');
  await h.api.get('/later');
  assert.equal(h.calls.refresh.length, 2);
  assert.equal(h.userStore.accessToken, 'new-token-2');
});

test('갱신 응답이 잘못되면 토큰을 저장하거나 원래 요청을 재시도하지 않는다', async () => {
  for (const data of ['', ' ', { token: 'unexpected' }]) {
    const h = createHarness({
      handleRefresh: config => response(config, data),
    });
    await assert.rejects(h.api.get('/request'), /갱신 응답/);
    assert.equal(h.calls.saves, 0);
    assert.equal(h.calls.api.length, 1);
  }
});

test('갱신 대기 중 로그아웃하면 늦은 응답으로 로그인 상태를 복구하지 않는다', async () => {
  const gate = deferred();
  const h = createHarness({
    handleRefresh: async config => {
      await gate.promise;
      return response(config, 'new-token');
    },
  });
  const pending = assert.rejects(h.api.get('/request'), error =>
    axios.isCancel(error),
  );
  await h.refreshStarted.promise;
  h.userStore.clearAuthInfo();
  gate.resolve();
  await pending;
  assert.equal(h.userStore.accessToken, null);
  assert.equal(h.calls.saves, 0);
  assert.equal(h.calls.api.length, 1);
});

test('이전 계정의 갱신 실패가 새 계정의 로그인 상태를 삭제하지 않는다', async () => {
  const gate = deferred();
  const h = createHarness({
    handleRefresh: async config => {
      await gate.promise;
      throw httpError(config, 401, 'REFRESH_TOKEN_INVALID');
    },
  });
  const pending = assert.rejects(h.api.get('/request'));
  await h.refreshStarted.promise;
  h.userStore.setAuthInfo(
    2,
    'another-user',
    'another-token',
    'other@example.com',
    'ROLE_ADMIN',
  );
  gate.resolve();
  await pending;
  assert.equal(h.userStore.accessToken, 'another-token');
  assert.equal(h.calls.clears, 0);
  assert.equal(h.calls.api.length, 1);
});

test('로그아웃 후 도착한 만료 응답으로 새 갱신을 시작하지 않는다', async () => {
  const gate = deferred();
  const h = createHarness({
    handleApi: async config => {
      await gate.promise;
      throw httpError(config);
    },
  });
  const pending = assert.rejects(h.api.get('/late'));
  await setImmediate();
  h.userStore.clearAuthInfo();
  gate.resolve();
  await pending;
  assert.equal(h.calls.refresh.length, 0);
});
