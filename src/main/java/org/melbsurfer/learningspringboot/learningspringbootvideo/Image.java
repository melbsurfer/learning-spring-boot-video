package org.melbsurfer.learningspringboot.learningspringbootvideo;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * Created by adrake on 7/8/17.
 */
@Entity
public class Image {
    /**
     * @Id for JPA primary key
     * @GeneratedValue lets JPA generate primary keys as needed
     */
    @Id @GeneratedValue
    private Long id;

    private String name;

    /**
     * JPA requires a no argument constructor.  It is not the most
     * effective way to work with our domain objects.
     *
     * Therefore, it is marked private to discourage usage of it. Frameworks
     * may need it to populate objects, but we don't want to write
     * any business code dependent upon this constructor.
     */
    private Image(){}

    /**
     * Constructor
     * @param name
     *
     * A more convenient constructor call.  This way we can initialize
     * the name object directly with it.
     *
     * Note: We didn't add the Id field, because Hibernate is
     * going to autogenerate it for us.
     */
    public Image(String name){
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
