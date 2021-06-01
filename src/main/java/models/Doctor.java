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
public class Doctor implements Serializable {
    private ObjectId id;
    private String firstname;
    private String surname;
    private Integer salary;
    private Department department;
}
