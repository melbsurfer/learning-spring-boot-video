package org.melbsurfer.learningspringboot.learningspringbootvideo;

import org.codehaus.groovy.tools.shell.IO;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

@Controller
public class HomeController {

    // Base path for the routes
    private static final String BASE_PATH = "/images";

    // Pattern for our file names that is part of REST controller.
    // Uses the filename:.+ for the token so that we don't use
    // Spring's content negotiation.
    private static final String FILENAME = "{filename:.+}";

    private ImageService imageService;

    // Using constructor injection here
    public HomeController(ImageService imageService){

        this.imageService = imageService;

    }

    /**
     * Purpose: Allows to retrieve an image using the ImageService.
     *
     * Note: This is a REST endpoint via the @ResponseBody annotation.  Whatever is sent back will
     * be written into the response body.
     *
     * @param filename
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, value= BASE_PATH + "/"+ FILENAME + "/raw")
    @ResponseBody
    public ResponseEntity<?> oneRawImage(@PathVariable String filename) {

        try {
            // Use the imageService to hand back a Spring Resource
            Resource file = imageService.findOneImage(filename);
            /**
             * Return a HTTP response message using ResponseEntity's utility
             * methods.  In this case, we will send back an 'ok' message (200).
             */
            return ResponseEntity.ok()
                    .contentLength(file.contentLength())
                    .contentType(MediaType.IMAGE_JPEG)
                    .body(new InputStreamResource(file.getInputStream()));
        } catch (IOException e) {
            return ResponseEntity.badRequest()
                    .body("Couldn't find " + filename + " => " + e.getMessage());
        }


    }

    /**
     *
     * Purpose:  Expect a POST request parameter of 'file' to be a multi-part file that
     * will use the ImageService to store the file.  Then we create a message header of
     * where the newly created image is stored, and can be fetched from.
     *
     * @param file
     * @param request
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, value = BASE_PATH)
    @ResponseBody
    public ResponseEntity<?> createFile(@RequestParam("file") MultipartFile file, HttpServletRequest servletRequest) throws URISyntaxException {

        try {

            imageService.createImage(file);
            final URI locationUri = new URI(servletRequest.getRequestURL().toString() + "/")
                    .resolve(file.getOriginalFilename() + "/raw");
            return ResponseEntity.created(locationUri)
                    .body("Successfully uploaded " + file.getOriginalFilename());
        } catch (IOException e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to upload " + file.getOriginalFilename() + " => " + e.getMessage());
        }

    }

    /**
     * Purpose: Removes and image from the database and the file systems
     * using the imageService.
     *
     * @param filename
     * @return
     */
    @RequestMapping(method = RequestMethod.DELETE, value = BASE_PATH + "/" + FILENAME)
    @ResponseBody
    public ResponseEntity<?> deleteFile(@PathVariable String filename) {


        try {
            imageService.deleteImage(filename);
            return ResponseEntity.status(HttpStatus.NO_CONTENT)
                    .body("Successfully deleted " + filename);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to delete " + filename + " => " + e.getMessage());
        }

    }
}
