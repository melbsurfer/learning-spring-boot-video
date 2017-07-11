package org.melbsurfer.learningspringboot.learningspringbootvideo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Profile;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 *  @Service Spring Boot component scanning will
 *  spot the object, and automatically create an instance
 *  of it.
 *
 *  Purpose: Conducts operations on the disk store, and the database
 */
@Service //
public class ImageService {

    /**
     * Target folder where images are uploaded.
     */
    private static String UPLOAD_ROOT = "upload-dir";

    private final ImageRepository repository;
    private final ResourceLoader resourceLoader;
    /**
     *
     * @Autowired Invokes the constructor method passing in the
     * params
     *
     * Purpose: Constructor
     *
     * Uses Spring Framework recommended constructor based injection.
     *
     * @param repository reference to the Image repository
     * @param resourceLoader used to interact with the file system
     */
    @Autowired
    public ImageService(ImageRepository repository, ResourceLoader resourceLoader){

        this.repository = repository;
        this.resourceLoader = resourceLoader;
    }

    /**
     * Purpose: Look up a single image by file name using
     * the resource loader.
     *
     * @param filename
     * @return
     */
    public Resource findOneImage(String filename) {
        return resourceLoader.getResource("file:" + UPLOAD_ROOT + "/" + filename);
    }

    /**
     * Purpose: Method that expects a MultipartFile object
     *
     * @param file
     * @throws IOException
     */
    public void createImage(MultipartFile file) throws IOException {

        if (!file.isEmpty()){
            // Copies file into the file store
            Files.copy(file.getInputStream(), Paths.get(UPLOAD_ROOT, file.getOriginalFilename()));
            // Creates a new entry in the database with the same heading
            repository.save(new Image(file.getOriginalFilename()));

        }

    }

    /**
     *
     * Purpose: Removes the image from the database and the file system.
     *
     * @param filename
     * @throws IOException
     */
    public void deleteImage(String filename) throws IOException {

        // Uses custom finder from the repository
        final Image byName = repository.findByName(filename);
        // Removes image from database
        repository.delete(byName);
        // Uses Java nio operation to remove the image from the file
        // system
        Files.deleteIfExists(Paths.get(UPLOAD_ROOT, filename));

    }

    /**
     *
     * Purpose: Pre-stages three test images onto the files
     * system, and into the database.
     *
     * Note: CommandLineRunner is automatically invoked once all of
     * the Beans in an app context are defined.
     *
     * @param repository
     * @return
     * @throws IOException
     */
    @Bean
    //@Profile("dev") // Runs only in development mode
    CommandLineRunner setUp(ImageRepository repository) throws IOException {

        // Use a Lambda to have the CommandLineRunner execute the clean
        // up and creation of the test images
        return(args) -> {

            // First, clean out the files root folder.
            FileSystemUtils.deleteRecursively(new File(UPLOAD_ROOT));

            // Make the directory for our image files
            Files.createDirectory(Paths.get(UPLOAD_ROOT));

            FileCopyUtils.copy("Test file", new FileWriter(UPLOAD_ROOT + "/test"));
            repository.save(new Image("test"));

            FileCopyUtils.copy("Test file2", new FileWriter(UPLOAD_ROOT + "/test2"));
            repository.save(new Image("test2"));

            FileCopyUtils.copy("Test file3", new FileWriter(UPLOAD_ROOT + "/test2"));
            repository.save(new Image("test3"));


        };


    }

}
