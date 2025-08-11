import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordTest {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        
        String plainPassword = "123456";
        String storedPassword = "$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi";
        
        System.out.println("明文密码: " + plainPassword);
        System.out.println("存储密码: " + storedPassword);
        System.out.println("密码长度: " + storedPassword.length());
        System.out.println("验证结果: " + encoder.matches(plainPassword, storedPassword));
        
        // 生成新的正确密码
        String newEncoded = encoder.encode(plainPassword);
        System.out.println("新生成密码: " + newEncoded);
        System.out.println("新密码验证: " + encoder.matches(plainPassword, newEncoded));
    }
}