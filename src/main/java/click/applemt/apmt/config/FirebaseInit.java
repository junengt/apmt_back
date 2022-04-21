package click.applemt.apmt.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.ResourceUtils;

import javax.annotation.PostConstruct;
import java.io.*;

@Slf4j
@Configuration
public class FirebaseInit {

    @Autowired
    private ResourceLoader resourceLoader;


    @Bean
    public FirebaseAuth initFirebaseService() throws IOException {

        InputStream inputStream = resourceLoader.getResource("classpath:/static/fkey.json").getInputStream();

        FileInputStream serviceAccount =
                new FileInputStream(convertInputStreamToFile(inputStream));
        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .build();
        return FirebaseAuth.getInstance(FirebaseApp.initializeApp(options));
    }

    public static File convertInputStreamToFile(InputStream in) throws IOException {

        File tempFile = File.createTempFile(String.valueOf(in.hashCode()), ".tmp");
        tempFile.deleteOnExit();

        copyInputStreamToFile(in, tempFile);

        return tempFile;
    }
    private static void copyInputStreamToFile(InputStream inputStream, File file) throws IOException {

        FileUtils.copyInputStreamToFile(inputStream, file);
    }

}
