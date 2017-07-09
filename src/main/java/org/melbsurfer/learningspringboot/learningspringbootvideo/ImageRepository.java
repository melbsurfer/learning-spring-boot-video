package org.melbsurfer.learningspringboot.learningspringbootvideo;


import org.springframework.data.repository.PagingAndSortingRepository;

/**
 *  JPA's PagingAndSortingRepository has two generic parameters that
 *  it applies over.  In this case the Image, and the Id type
 *  which is a Long.
 */
public interface ImageRepository extends PagingAndSortingRepository<Image, Long> {

    /**
     * A JPA finder that is meant to return an image.
     *
     * @param name
     * @return
     */
    public Image findByName(String name);

}
