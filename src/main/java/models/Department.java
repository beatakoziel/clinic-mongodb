package models;

import lombok.*;
import org.bson.types.ObjectId;

import java.io.Serializable;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Department implements Serializable {
    private ObjectId id;
    private String name;
    private Integer creationYear;
}
