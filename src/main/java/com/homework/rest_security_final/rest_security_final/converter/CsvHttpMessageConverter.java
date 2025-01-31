package com.homework.rest_security_final.rest_security_final.converter;

import com.homework.rest_security_final.rest_security_final.model.Member;
import com.homework.rest_security_final.rest_security_final.model.Role;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.*;
import java.util.Objects;

@Slf4j
public class CsvHttpMessageConverter extends AbstractHttpMessageConverter<Member> {

    public CsvHttpMessageConverter() {
        super(new MediaType("text", "csv"));
    }

    @Override
    protected boolean supports(Class<?> clazz) {
        return Member.class.isAssignableFrom(clazz);
    }

    @Override
    protected Member readInternal(Class<? extends Member> clazz, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputMessage.getBody()));
        Member member = null;
        String line;

        while((line=reader.readLine()) != null) {
            String[] lines = line.split(",");

            member = new Member (
                    lines[0].trim(),
                    lines[1].trim(),
                    lines[2].trim(),
                    Integer.parseInt(lines[3].trim()),
                    Role.valueOf(lines[4].trim())
            );

        }
        if(Objects.isNull(member)) {
            throw new IllegalArgumentException();
        }
        return member;
    }

    // *** false면 text/csv 파싱 안함 ***
    @Override
    protected boolean canRead(MediaType mediaType) {
        return true;
    }

    @Override
    protected void writeInternal(Member member, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
        outputMessage.getHeaders().setContentType(MediaType.valueOf("text/csv;charset=UTF-8"));
        try (Writer writer = new OutputStreamWriter(outputMessage.getBody())) {
            writer.write("id,name,password,age,role ");
            writer.write(member.getId() + "," + member.getName()+ "," +member.getPassword()+ "," +member.getAge()+ "," +member.getRole());
            writer.flush();
        }
    }

}
