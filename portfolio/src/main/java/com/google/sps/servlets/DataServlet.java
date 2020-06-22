// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps.servlets;

import java.io.IOException;
import java.util.ArrayList;
import com.google.appengine.api.blobstore.BlobInfo;
import com.google.appengine.api.blobstore.BlobInfoFactory;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.images.ImagesService;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.appengine.api.images.ServingUrlOptions;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.gson.Gson;
import com.google.cloud.vision.v1.AnnotateImageRequest;
import com.google.cloud.vision.v1.AnnotateImageResponse;
import com.google.cloud.vision.v1.BatchAnnotateImagesResponse;
import com.google.cloud.vision.v1.EntityAnnotation;
import com.google.cloud.vision.v1.Feature;
import com.google.cloud.vision.v1.Image;
import com.google.cloud.vision.v1.ImageAnnotatorClient;
import com.google.protobuf.ByteString;
import java.io.ByteArrayOutputStream;
import com.google.cloud.language.v1.Document;
import com.google.cloud.language.v1.LanguageServiceClient;
import com.google.cloud.language.v1.Sentiment;
import java.util.List;
import java.util.Map;
import com.google.sps.data.Comment;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that returns some example content. TODO: modify this file to handle comments data */
@WebServlet("/chat-room")
public class DataServlet extends HttpServlet {

  @Override
  public void init() {
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.setContentType("application/json");

    Query query = new Query("Comment").addSort("timestamp", SortDirection.DESCENDING);

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery results = datastore.prepare(query);
    System.out.println(results);
    List<Comment> comments = new ArrayList<>();
    for (Entity entity : results.asIterable()) {
      long id = entity.getKey().getId();
      String sender = (String) entity.getProperty("sender");
      String message = (String) entity.getProperty("message");
      String email = (String) entity.getProperty("email");
      String imageUrl = (String) entity.getProperty("image");
      List<EntityAnnotation> imageLabels = (List<EntityAnnotation>) entity.getProperty("imagelabel");
      float score = (float) entity.getProperty("sentiment");
      Comment comment = new Comment(sender, message, email, imageUrl, imageLabels, score);
      comments.add(comment);
    }
    Gson gson = new Gson();
    String json = new Gson().toJson(comments);
    response.getWriter().println(json);
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    UserService userService = UserServiceFactory.getUserService();
    // Only logged-in users can post messages
    if (!userService.isUserLoggedIn()) {
      response.sendRedirect("/chat-room");
      return;
    }
    addNewComment(request, userService);
    // Redirect back to the HTML page.
    response.sendRedirect("/chatroom.html");
  }

  private void addNewComment(HttpServletRequest request, UserService userService) throws IOException{
      String tempSubject = request.getParameter("subject");
      String commentText = request.getParameter("text");
      String email = userService.getCurrentUser().getEmail();
      String imageUrl; byte[] blobBytes; List<EntityAnnotation> imageLabels;
      BlobKey blobKey = getBlobKey(request, "image");
          imageUrl = getUploadedFileUrl(blobKey);
          blobBytes = getBlobBytes(blobKey);
          imageLabels = getImageLabels(blobBytes);

       Document doc = Document.newBuilder().setContent(commentText).setType(Document.Type.PLAIN_TEXT).build();
       LanguageServiceClient languageService = LanguageServiceClient.create();
       Sentiment sentiment = languageService.analyzeSentiment(doc).getDocumentSentiment();
       float score = sentiment.getScore();
       languageService.close();
      
      Entity commentEntity = new Entity("Comment");
     commentEntity.setProperty("sender", tempSubject);
     commentEntity.setProperty("message", commentText);
     commentEntity.setProperty("email", email);
     commentEntity.setProperty("image", imageUrl);
     commentEntity.setProperty("timestamp", System.currentTimeMillis());
     commentEntity.setProperty("imagelabel", imageLabels);
     commentEntity.setProperty("sentiment", score);

      DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
      datastore.put(commentEntity);
  }

//    /** Returns a URL that points to the uploaded file, or null if the user didn't upload a file. */
//   private String getUploadedFileUrl(HttpServletRequest request, String formInputElementName) {
//     BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
//     Map<String, List<BlobKey>> blobs = blobstoreService.getUploads(request);
//     List<BlobKey> blobKeys = blobs.get("image");

//     // User submitted form without selecting a file, so we can't get a URL. (dev server)
//     if (blobKeys == null || blobKeys.isEmpty()) {
//       return null;
//     }

//     // Our form only contains a single file input, so get the first index.
//     BlobKey blobKey = blobKeys.get(0);

//     // User submitted form without selecting a file, so we can't get a URL. (live server)
//     BlobInfo blobInfo = new BlobInfoFactory().loadBlobInfo(blobKey);
//     if (blobInfo.getSize() == 0) {
//       blobstoreService.delete(blobKey);
//       return null;
//     }

//     // We could check the validity of the file here, e.g. to make sure it's an image file
//     // https://stackoverflow.com/q/10779564/873165

//     // Use ImagesService to get a URL that points to the uploaded file.
//     ImagesService imagesService = ImagesServiceFactory.getImagesService();
//     ServingUrlOptions options = ServingUrlOptions.Builder.withBlobKey(blobKey);
//     String url = imagesService.getServingUrl(options);

//     // GCS's localhost preview is not actually on localhost,
//     // so make the URL relative to the current domain.
//     if(url.startsWith("http://localhost:8080/")){
//       url = url.replace("http://localhost:8080/", "/");
//     }
//     return url;
//   }

   /**
   * Returns the BlobKey that points to the file uploaded by the user, or null if the user didn't
   * upload a file.
   */
  private BlobKey getBlobKey(HttpServletRequest request, String formInputElementName) {
    BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
    Map<String, List<BlobKey>> blobs = blobstoreService.getUploads(request);
    List<BlobKey> blobKeys = blobs.get("image");

    // User submitted form without selecting a file, so we can't get a BlobKey. (dev server)
    if (blobKeys == null || blobKeys.isEmpty()) {
      return null;
    }

    // Our form only contains a single file input, so get the first index.
    BlobKey blobKey = blobKeys.get(0);

    // User submitted form without selecting a file, so the BlobKey is empty. (live server)
    BlobInfo blobInfo = new BlobInfoFactory().loadBlobInfo(blobKey);
    if (blobInfo.getSize() == 0) {
      blobstoreService.delete(blobKey);
      return null;
    }

    return blobKey;
  }

  /**
   * Blobstore stores files as binary data. This function retrieves the binary data stored at the
   * BlobKey parameter.
   */
  private byte[] getBlobBytes(BlobKey blobKey) throws IOException {
    BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
    ByteArrayOutputStream outputBytes = new ByteArrayOutputStream();

    int fetchSize = BlobstoreService.MAX_BLOB_FETCH_SIZE;
    long currentByteIndex = 0;
    boolean continueReading = true;
    while (continueReading) {
      // end index is inclusive, so we have to subtract 1 to get fetchSize bytes
      byte[] b =
          blobstoreService.fetchData(blobKey, currentByteIndex, currentByteIndex + fetchSize - 1);
      outputBytes.write(b);

      // if we read fewer bytes than we requested, then we reached the end
      if (b.length < fetchSize) {
        continueReading = false;
      }

      currentByteIndex += fetchSize;
    }

    return outputBytes.toByteArray();
  }

  /**
   * Uses the Google Cloud Vision API to generate a list of labels that apply to the image
   * represented by the binary data stored in imgBytes.
   */
  private List<EntityAnnotation> getImageLabels(byte[] imgBytes) throws IOException {
    ByteString byteString = ByteString.copyFrom(imgBytes);
    Image image = Image.newBuilder().setContent(byteString).build();

    Feature feature = Feature.newBuilder().setType(Feature.Type.LABEL_DETECTION).build();
    AnnotateImageRequest request =
        AnnotateImageRequest.newBuilder().addFeatures(feature).setImage(image).build();
    List<AnnotateImageRequest> requests = new ArrayList<>();
    requests.add(request);

    ImageAnnotatorClient client = ImageAnnotatorClient.create();
    BatchAnnotateImagesResponse batchResponse = client.batchAnnotateImages(requests);
    client.close();
    List<AnnotateImageResponse> imageResponses = batchResponse.getResponsesList();
    AnnotateImageResponse imageResponse = imageResponses.get(0);

    if (imageResponse.hasError()) {
      System.err.println("Error getting image labels: " + imageResponse.getError().getMessage());
      return null;
    }

    return imageResponse.getLabelAnnotationsList();
  }

  /** Returns a URL that points to the uploaded file. */
  private String getUploadedFileUrl(BlobKey blobKey) {
    ImagesService imagesService = ImagesServiceFactory.getImagesService();
    ServingUrlOptions options = ServingUrlOptions.Builder.withBlobKey(blobKey);
    String url = imagesService.getServingUrl(options);

    // GCS's localhost preview is not actually on localhost,
    // so make the URL relative to the current domain.
    if(url.startsWith("http://localhost:8080/")){
      url = url.replace("http://localhost:8080/", "/");
    }
    return url;
  }

}
