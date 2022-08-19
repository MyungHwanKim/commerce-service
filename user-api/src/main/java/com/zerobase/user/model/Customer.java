package com.zerobase.user.model;

import com.zerobase.user.domain.SignUpForm;
import lombok.*;
import org.hibernate.envers.AuditOverride;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Locale;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@AuditOverride(forClass = BaseEntity.class)
@Entity
public class Customer extends BaseEntity {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String email;
    private String name;
    private String password;
    private String phone;
    private LocalDate birth;

    private LocalDateTime verifyExpiredAt;
    private String verificationCode;
    private boolean verify;

    public static Customer from(SignUpForm form) {
        return Customer.builder()
                .email(form.getEmail().toLowerCase(Locale.ROOT))
                .name(form.getName())
                .password(form.getPassword())
                .phone(form.getPhone())
                .birth(form.getBirth())
                .verify(false)
                .build();
    }
}
