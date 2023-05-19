package org.example.domain;

import java.time.LocalDate;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.NamedEntityGraphs;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import org.springframework.data.jpa.repository.EntityGraph;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@NamedEntityGraphs(
  value = {
    @NamedEntityGraph(name = "withPhones", attributeNodes = @NamedAttributeNode("phoneNumbers"))
  }
)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    private String name;

    private LocalDate dateOfBirth;

    private String password;

    @OneToOne(mappedBy = "user")
    private Account account;

    @OneToMany(mappedBy = "user")
    private List<EmailData> emails;

    @OneToMany(mappedBy = "user")
    private List<PhoneData> phoneNumbers;
}