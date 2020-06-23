package com.rodrigopeleias.bookstoremanager.service;

import com.rodrigopeleias.bookstoremanager.builder.AuthorDTOBuilder;
import com.rodrigopeleias.bookstoremanager.dto.AuthorDTO;
import com.rodrigopeleias.bookstoremanager.entity.Author;
import com.rodrigopeleias.bookstoremanager.exception.AuthorAlreadyExistsException;
import com.rodrigopeleias.bookstoremanager.exception.AuthorNotFoundException;
import com.rodrigopeleias.bookstoremanager.mapper.AuthorMapper;
import com.rodrigopeleias.bookstoremanager.repository.AuthorRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthorServiceTest {

    private final AuthorMapper authorMapper = AuthorMapper.INSTANCE;

    @Mock
    private AuthorRepository authorRepository;

    @InjectMocks
    private AuthorService authorService;

    @Test
    void whenNewAuthorIsInformedThenItShouldBeCreated() throws AuthorAlreadyExistsException {
        AuthorDTO expectedAuthorToCreateDTO = AuthorDTOBuilder.builder().build().buildAuthorDTO();
        Author expectedCreatedAuthor = authorMapper.toModel(expectedAuthorToCreateDTO);

        when(authorRepository.save(expectedCreatedAuthor)).thenReturn(expectedCreatedAuthor);
        when(authorRepository.findByName(expectedAuthorToCreateDTO.getName())).thenReturn(Optional.empty());

        AuthorDTO createdAuthorDTO = authorService.create(expectedAuthorToCreateDTO);

        assertThat(createdAuthorDTO, is(equalTo(expectedAuthorToCreateDTO)));
    }

    @Test
    void whenExistingAuthorIsInformedThenThrowsAnException() {
        AuthorDTO expectedAuthorToCreateDTO = AuthorDTOBuilder.builder().build().buildAuthorDTO();
        Author expectedDuplicatedAuthor = authorMapper.toModel(expectedAuthorToCreateDTO);

        when(authorRepository.findByName(expectedAuthorToCreateDTO.getName())).thenReturn(Optional.of(expectedDuplicatedAuthor));

        assertThrows(AuthorAlreadyExistsException.class, () -> authorService.create(expectedAuthorToCreateDTO));
    }

    @Test
    void whenValidNameIsGivenThenReturnAnAuthor() throws AuthorNotFoundException {
        AuthorDTO expectedFoundAuthorDTO = AuthorDTOBuilder.builder().build().buildAuthorDTO();
        Author expectedFoundAuthor = authorMapper.toModel(expectedFoundAuthorDTO);

        when(authorRepository.findByName(expectedFoundAuthorDTO.getName())).thenReturn(Optional.of(expectedFoundAuthor));

        AuthorDTO foundAuthor = authorService.findByName(expectedFoundAuthorDTO.getName());

        assertThat(foundAuthor, is(equalTo(expectedFoundAuthorDTO)));
    }

    @Test
    void whenInvalidNameIsGivenThenThrowAnException() {
        AuthorDTO expectedFoundAuthorDTO = AuthorDTOBuilder.builder().build().buildAuthorDTO();

        when(authorRepository.findByName(expectedFoundAuthorDTO.getName())).thenReturn(Optional.empty());

        assertThrows(AuthorNotFoundException.class, () -> authorService.findByName(expectedFoundAuthorDTO.getName()));
    }
}
