package click.applemt.apmt.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.io.FileInputStream;
import java.io.IOException;

@Configuration
public class FirebaseInit {

    @Bean
    public FirebaseAuth initFirebaseService() throws IOException {
        FileInputStream serviceAccount =
                new FileInputStream("src/main/resources/firebasekey.json");
        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .build();
        return FirebaseAuth.getInstance(FirebaseApp.initializeApp(options));
    }

}
