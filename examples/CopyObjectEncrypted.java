import java.lang.StringBuilder;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import java.security.NoSuchAlgorithmException;
import java.security.InvalidKeyException;
import javax.crypto.spec.SecretKeySpec;
import org.xmlpull.v1.XmlPullParserException;

import io.minio.MinioClient;
import io.minio.ServerSideEncryption;
import io.minio.errors.MinioException;

public class CopyObjectEncrypted {
  /**
   * MinioClient.copyObject() example using SSE_C.
   */
  public static void main(String[] args) 
      throws NoSuchAlgorithmException, IOException, InvalidKeyException, XmlPullParserException {
    try {
      /* play.minio.io for test and development. */
      MinioClient minioClient = new MinioClient("https://play.min.io:9000", "Q3AM3UQ867SPQQA43P2F",
                                                "zuf+tfteSlswRu7BJ86wekitnifILbZam1KYY3TG");

      /* Amazon S3: */
      // MinioClient minioClient = new MinioClient("https://s3.amazonaws.com", "YOUR-ACCESSKEYID",
      //                                           "YOUR-SECRETACCESSKEY");

      // Create some content for the object.
      StringBuilder builder = new StringBuilder();
      for (int i = 0; i < 1000; i++) {
        builder.append("Sphinx of black quartz, judge my vow: Used by Adobe InDesign to display font samples. ");
        builder.append("(29 letters)\n");
        builder.append("Jackdaws love my big sphinx of quartz: Similarly, used by Windows XP for some fonts. ");
        builder.append("(31 letters)\n");
        builder.append("Pack my box with five dozen liquor jugs: According to Wikipedia, this one is used on ");
        builder.append("NASAs Space Shuttle. (32 letters)\n");
        builder.append("The quick onyx goblin jumps over the lazy dwarf: Flavor text from an Unhinged Magic Card. ");
        builder.append("(39 letters)\n");
        builder.append("How razorback-jumping frogs can level six piqued gymnasts!: Not going to win any brevity ");
        builder.append("awards at 49 letters long, but old-time Mac users may recognize it.\n");
        builder.append("Cozy lummox gives smart squid who asks for job pen: A 41-letter tester sentence for Mac ");
        builder.append("computers after System 7.\n");
        builder.append("A few others we like: Amazingly few discotheques provide jukeboxes; Now fax quiz Jack! my ");
        builder.append("brave ghost pled; Watch Jeopardy!, Alex Trebeks fun TV quiz game.\n");
        builder.append("---\n");
      }
      // Create a InputStream for object upload.
      ByteArrayInputStream bais = new ByteArrayInputStream(builder.toString().getBytes("UTF-8"));
        
      // Generate a new 256 bit AES key - This key must be remembered by the client.
      byte[] key = "01234567890123456789012345678901".getBytes("UTF-8"); 
      SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
        
      ServerSideEncryption ssePut = ServerSideEncryption.withCustomerKey(secretKeySpec);
      ServerSideEncryption sseSource = ServerSideEncryption.copyWithCustomerKey(secretKeySpec);
        
      byte[] keyTarget = "98765432100123456789012345678901".getBytes("UTF-8"); 
      SecretKeySpec secretKeySpecTarget = new SecretKeySpec(keyTarget, "AES");

      ServerSideEncryption sseTarget = ServerSideEncryption.withCustomerKey(secretKeySpecTarget);
        
      // Create object 'my-objectname' in 'my-bucketname' with content from the input stream.
      minioClient.putObject("my-bucketname", "my-objectname", bais, bais.available(), ssePut);
      System.out.println("my-objectname is uploaded successfully");
        
      minioClient.copyObject("my-bucketname", "my-objectname", sseSource, "my-destbucketname",
                             "my-objectname-copy", null, sseTarget);

      bais.close();

      System.out.println("my-objectname-copy copied to my-destbucketname successfully.");
    } catch (MinioException e) {
      System.out.println("Error occurred: " + e);
    }
    System.out.println("finished");
  }
}