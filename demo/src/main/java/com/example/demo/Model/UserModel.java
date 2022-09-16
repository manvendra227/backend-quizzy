package com.example.demo.Model;

import com.example.demo.Collection.extras.Gender;
import com.example.demo.Collection.extras.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserModel {
    private String name;
    private String emailId;
    private String dob;
    private String password;
    private String matchingPassword;
    private Gender gender;
    private Status status;
}
