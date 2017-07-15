package checkdrive;



import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.*;
import com.google.api.services.drive.Drive;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GoogleDriveTest {
    /** Application name. */
    private static final String APPLICATION_NAME =
        "Drive API Java Quickstart";

    /** Directory to store user credentials for this application. */
    private static final java.io.File DATA_STORE_DIR = new java.io.File("C:\\Users\\T430\\pruebasDrive");

    /** Global instance of the {@link FileDataStoreFactory}. */
    private static FileDataStoreFactory DATA_STORE_FACTORY;

    /** Global instance of the JSON factory. */
    private static final JsonFactory JSON_FACTORY =
        JacksonFactory.getDefaultInstance();

    /** Global instance of the HTTP transport. */
    private static HttpTransport HTTP_TRANSPORT;

    /** Global instance of the scopes required by this quickstart.
     *
     * If modifying these scopes, delete your previously saved credentials
     * at ~/.credentials/drive-java-quickstart
     */
    private static final List<String> SCOPES =
        Arrays.asList(DriveScopes.DRIVE_APPDATA,DriveScopes.DRIVE, DriveScopes.DRIVE_FILE, DriveScopes.DRIVE_METADATA, DriveScopes.DRIVE_METADATA_READONLY, DriveScopes.DRIVE_READONLY);

    static {
        try {
            HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            DATA_STORE_FACTORY = new FileDataStoreFactory(DATA_STORE_DIR);
        } catch (Throwable t) {
            t.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Creates an authorized Credential object.
     * @return an authorized Credential object.
     * @throws IOException
     */
    public static Credential authorize() throws IOException {
        // Load client secrets.
        InputStream in =new FileInputStream("C:\\Users\\T430\\pruebasDrive\\cliente2.json");
        GoogleClientSecrets clientSecrets =
            GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.

 //       SCOPES.add(DriveScopes.DRIVE_APPDATA);
 //       SCOPES.add(DriveScopes.DRIVE_FILE);
 //       SCOPES.add(DriveScopes.DRIVE_METADATA);
 //       SCOPES.add(DriveScopes.DRIVE_METADATA_READONLY);
 //       SCOPES.add(DriveScopes.DRIVE_READONLY);
        GoogleAuthorizationCodeFlow flow =
                new GoogleAuthorizationCodeFlow.Builder(
                		HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(DATA_STORE_FACTORY)
                .setAccessType("offline")
                .build();
        Credential credential = new AuthorizationCodeInstalledApp(
            flow, new LocalServerReceiver()).authorize("user");
        System.out.println(
                "Credentials saved to " + DATA_STORE_DIR.getAbsolutePath());
        return credential;
    }

    /**
     * Build and return an authorized Drive client service.
     * @return an authorized Drive client service
     * @throws IOException
     */
    public  Drive getDriveService() throws IOException {
        Credential credential = authorize();
        return new Drive.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }
	
	public  Drive getDriveService1() throws IOException {
		Credential credential = authorize();
        return new Drive.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    public static void main(String[] args) throws IOException {
        // Build a new authorized API client service.
    	//el folder debe de estar compartido ejemplo de entrada : https://drive.google.com/open?id=0BxtsR0K_3ysdflFQZzZfZTZnakZ5OTBZbEprUHNVRTQ0a1ZqNjJ5NmFramV4ZVg3dE5pWW8

        GoogleDriveTest g = new GoogleDriveTest();
        System.out.println("obteniendo servicio");
    	Drive service = g.getDriveService();
        System.out.println("obteniendo drive");
        System.out.println("iniciando verificacion");
        StartPageToken response = service.changes().getStartPageToken().execute();

        System.out.println("Start token: " + response.getStartPageToken());
        System.out.println("Archivos que cambiaron: " + g.checkChangesInFolder(service, g.getIdFromUrlGDriveShared("https://drive.google.com/open?id=0BxtsR0K_3ysdSjFveWJnUTk5Tmc")));//"https://drive.google.com/open?id=0BxtsR0K_3ysdSjFveWJnUTk5Tmc")));
        
    }
    
    
    public List<File> checkChangesInFolder(Drive service, String folderId){
    	List<File> fileList= new ArrayList<File>();
		try {
	    	 FileList result;
//				result = service.files().list().setQ("'" + folderId + "' in parents and trashed = false and (mimeType contains 'pdf' or mimeType contains 'jpeg' or mimeType contains 'png')").setFields("files(id, name, kind, mimeType, webContentLink)").execute();
	    	 	result = service.files().list().setQ("'" + folderId + "' in parents and trashed = false ").setFields("files(id, name, webViewLink, mimeType, webContentLink, imageMediaMetadata)").execute();
				List<File> files = result.getFiles();
		    if (files == null || files.size() == 0) {
		        System.out.println("No files found.");
		    } else {
		        for (File file : files) {
		        	System.out.println("FILE: " + file);
		        	if(!file.getMimeType().toLowerCase().contains("pdf")){
		        		continue;
		        	}
//		        	if(isNotTrashFile(file)){
//			        	OutputStream out = new FileOutputStream("C:\\Users\\martin_manzano\\Desktop\\test_gdrive\\"+ file.getName());
//			        	service.files().get(file.getId())
//			        	        .executeMediaAndDownloadTo(out);
//		        	}
		        }
		    }
		    return fileList;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new ArrayList<File>();
		}
    }
    
    public boolean isNotTrashFile(File file) {
        return (file.getTrashed()!=null);
    }
    
    public List<File> detectChanges(Drive service) throws IOException{

       // StartPageToken response = service.changes().getStartPageToken().execute();
    	//String pageToken = response.getStartPageToken();
    	String pageToken = "67961";
    	List<File> fileList= new ArrayList<File>();
		try {
	        while (pageToken != null) {
	            ChangeList changes;
					changes = service.changes().list(pageToken)
					        .execute();
	            for (Change change : changes.getChanges()) {
	                fileList.add(change.getFile());
	            }
	            if (changes.getNewStartPageToken() != null) {
	                // Last page, save this token for the next polling interval
	                System.out.println(changes.getNewStartPageToken());
	            }
	            pageToken = changes.getNextPageToken();
	        }
	        return fileList;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new ArrayList<File>();
		}
		
    }

    public String getIdFromUrlGDriveShared(String url){
    	//https://drive.google.com/open?id=0BxtsR0K_3ysdSjFveWJnUTk5Tmc
    	String id = "";
    	String params = (url.split("\\?").length>1)?url.split("\\?")[1]: "";
    	if(params.contains("id")&&params.contains("=")){
    		id = params.split("=")[1];
    	}
    	System.out.println("DRIVEID: " + id);
    	return id;
    }

    public String downloadFile(Drive service, String id) {
    	   if (service != null && id != null) 
    		   try {
    	     com.google.api.services.drive.model.File gFl =
    	        service.files().get(id).setFields("downloadUrl").execute();
    	     return gFl.getName();
    	    } catch (Exception e) { 
    	    	e.printStackTrace(); 
    	    	return "";
    	    }
    	   return "";
    	   
    	  
    }
}