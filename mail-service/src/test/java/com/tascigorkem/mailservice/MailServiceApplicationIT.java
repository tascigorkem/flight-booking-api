package com.tascigorkem.mailservice;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class MailServiceApplicationIT {

	@Test
	void main() {
		assertNotNull(MailServiceApplication.class);
		MailServiceApplication.main(new String[]{});
	}

}
