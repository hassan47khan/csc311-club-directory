package edu.farmingdale.csc311.clubdirectory.club;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

/** Runs against the three starter clubs; {@code @Transactional} rolls back anything a test adds. */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class ClubControllerTests {

    @Autowired
    private MockMvc mvc;

    private ResultActions postClub(String json) throws Exception {
        return mvc.perform(post("/api/clubs").contentType(MediaType.APPLICATION_JSON).content(json));
    }

    @Test
    void listsAllClubs() throws Exception {
        mvc.perform(get("/api/clubs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)));
    }

    @Test
    void filtersByCategoryIgnoringCaseAndSurroundingSpaces() throws Exception {
        mvc.perform(get("/api/clubs").param("category", " academic "))
                .andExpect(jsonPath("$[*].name", contains("Computer Science Club")));
    }

    @Test
    void blankCategoryMeansNoFilter() throws Exception {
        mvc.perform(get("/api/clubs").param("category", ""))
                .andExpect(jsonPath("$", hasSize(3)));
    }

    @Test
    void unknownClubIsNotFoundWithAReason() throws Exception {
        mvc.perform(get("/api/clubs/999999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value("No club with id 999999"));
    }

    @Test
    void createsClubAndCleansUpItsText() throws Exception {
        postClub("""
                {"name":"  Robotics  ","category":" Academic ","meetingDay":"Monday","room":"   "}""")
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value("Robotics"))
                .andExpect(jsonPath("$.category").value("Academic"))
                .andExpect(jsonPath("$.room").value(nullValue()));
    }

    @Test
    void createIgnoresAnIdInTheRequest() throws Exception {
        postClub("""
                {"id":1,"name":"Robotics","category":"Academic","meetingDay":"Monday"}""")
                .andExpect(status().isCreated());

        mvc.perform(get("/api/clubs/1"))
                .andExpect(jsonPath("$.name").value("Computer Science Club"));
    }

    @Test
    void reportsEveryMissingField() throws Exception {
        postClub("""
                {"name":"  ","category":"","meetingDay":""}""")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors", contains(
                        "category is required", "meetingDay is required", "name is required")));
    }

    @Test
    void reportsFieldThatIsTooLong() throws Exception {
        postClub("""
                {"name":"Robotics","category":"Academic","meetingDay":"Monday","room":"%s"}"""
                .formatted("x".repeat(41)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors", contains("room must be at most 40 characters")));
    }
}
