import java.util.ArrayList;
import java.util.List;

public class OomTest {
    public static void main(String[] args) {
        System.out.println("OOM 테스트를 시작합니다....");
        List<byte[]> list = new ArrayList<>();
        
        while (true) {
            // 1MB 크기의 바이트 배열을 계속 생성해서 리스트에 추가
            list.add(new byte[1024 * 1024]); 
        }
    }
}