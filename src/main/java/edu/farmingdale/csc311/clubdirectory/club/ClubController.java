package edu.farmingdale.csc311.clubdirectory.club;

import jakarta.validation.Valid;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/clubs")
public class ClubController {

    private final ClubRepository repository;

    public ClubController(ClubRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<Club> getClubs(@RequestParam(required = false) String category) {
        if (StringUtils.hasText(category)) {
            return repository.findByCategoryIgnoreCase(category.strip());
        }
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public Club getClub(@PathVariable Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No club with id " + id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Club addClub(@Valid @RequestBody Club club) {
        Club newClub = new Club(club.getName(), club.getCategory(), club.getMeetingDay(), club.getRoom());
        return repository.save(newClub);
    }

    // Adds the field messages to the 400 response so the page can show what is actually wrong.
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleInvalidClub(MethodArgumentNotValidException exception) {
        ProblemDetail problem = exception.getBody();
        problem.setProperty("errors", exception.getBindingResult().getFieldErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .sorted()
                .toList());
        return problem;
    }
}
