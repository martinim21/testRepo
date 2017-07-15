package checkdrive;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;

import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.*;
import com.google.api.services.drive.Drive;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class GoogleDriveBasic {
	  private String redirectURI;
		HttpTransport httpTransport;
		JsonFactory jsonFactory;
		GoogleAuthorizationCodeFlow flow;
		Drive service;

		
		/**
		 * Initialize initials attributes.
		 * 
		 * @param String basic configuration parameters.
		 */
		public GoogleDriveBasic(String CLIENT_ID, String CLIENT_SECRET, String REDIRECT_URI){
			this.redirectURI=REDIRECT_URI;
			httpTransport = new NetHttpTransport();
			jsonFactory= new JacksonFactory();
			
			flow= new GoogleAuthorizationCodeFlow.Builder(
					httpTransport, jsonFactory, 
					CLIENT_ID, CLIENT_SECRET, 
					Arrays.asList(DriveScopes.DRIVE))
					.setAccessType("online")
					.setApprovalPrompt("auto").build();
		}
		
		/**
		 * Get the authorization URL for authorize the application.
		 *
		 * @return String URL for authorize the application.
		 */
		public String getURL(){
			String url = flow.newAuthorizationUrl().setRedirectUri(redirectURI).build();
			return url;
		}
		
		
		/**
		 * Set the authorization code and create the service.
		 *
		 * @param String authorization code.
		 */
		public void setCode(String code) throws IOException{
			GoogleTokenResponse response = flow.newTokenRequest(code).setRedirectUri(redirectURI).execute();
			GoogleCredential credential = new GoogleCredential().setFromTokenResponse(response);

			//Create a new authorized API client
			service = new Drive.Builder(httpTransport, jsonFactory, credential).build();
		}

		
		/**
		 * Get the content of a file.
		 *
		 * @param File to get the content.
		 * @return String content of the file.
		 */
		public String downloadTextFile(File file) throws IOException{
			GenericUrl url = new GenericUrl(file.getWebContentLink());
			HttpResponse response = service.getRequestFactory().buildGetRequest(url).execute();
			try {
				return new Scanner(response.getContent()).useDelimiter("\\A").next();
			} catch (java.util.NoSuchElementException e) {
				return "";
			}
		}
		
		/**
		 * Get the content of a file.
		 *
		 * @param String the file ID.
		 * @return String content of the file.
		 */
		public String downloadTextFile(String fileID) throws IOException{
			File file=service.files().get(fileID).execute();
			return downloadTextFile(file);
		}
		
		/**
		 * Retrieve a list of File resources.
		 *
		 * @param service Drive API service instance.
		 * @return List of File resources.
		 * @author Google
		 * @throws IOException 
		 */
		public List<File> retrieveAllFiles() throws IOException {
			List<File> result = new ArrayList<File>();
			com.google.api.services.drive.Drive.Files.List request = null;

			request = service.files().list();


			do {
				try {
					FileList files = request.execute();

					result.addAll(files.getFiles());
					request.setPageToken(files.getNextPageToken());
				} catch (IOException e) {
					request.setPageToken(null);
				}
			} while (request.getPageToken() != null &&
					request.getPageToken().length() > 0);

			return result;
		}

	}
