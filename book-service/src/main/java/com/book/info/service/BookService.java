package com.book.info.service;


import com.book.info.GoodReadsBook;
import com.book.info.GoodReadsBook.Author;
import com.book.info.model.AuthorDto;
import com.book.info.model.AuthorDto.AuthorDtoBuilder;
import com.book.info.model.BookDto;
import com.book.info.model.BookDto.BookDtoBuilder;
import com.book.info.repository.BookRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Service;

@Service
public class BookService {

    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public Optional<BookDto> find(int bookId) {
        Optional<BookDto> optionalBookDto;
        try {
            Optional<GoodReadsBook> goodReadsBook = bookRepository.find(bookId);
            optionalBookDto = goodReadsBook.map(mapper);
        } catch (SQLException | JsonProcessingException e) {
            e.printStackTrace();
            return Optional.empty();
        }
        return optionalBookDto;
    }

    private Function<GoodReadsBook, BookDto> mapper = goodReadsBook -> {
        BookDtoBuilder bookDtoBuilder = BookDto.builder();
        bookDtoBuilder.averageRating(NumberUtils.toDouble(goodReadsBook.getAverageRating()))
            .id(NumberUtils.toInt(goodReadsBook.getId()))
            .countryCode(goodReadsBook.getCountryCode())
            .languageCode(goodReadsBook.getLanguageCode())
            .ebook(BooleanUtils.toBoolean(goodReadsBook.getIsEbook()))
            .format(goodReadsBook.getFormat())
            .description(goodReadsBook.getDescription())
            .link(goodReadsBook.getLink())
            .url(goodReadsBook.getUrl())
            .imageUrl(goodReadsBook.getImageUrl())
            .publisher(goodReadsBook.getPublisher())
            .pageCount(goodReadsBook.getPageCount())
            .isbn(goodReadsBook.getIsbn())
            .isbn13(goodReadsBook.getIsbn13())
            .title(goodReadsBook.getTitle())
            .publicationDate(goodReadsBook.getPublicationDate())
            .publicationYear(NumberUtils.toInt(goodReadsBook.getPublicationYear()))
            .editionInformation(goodReadsBook.getEditionInformation())
            .titleWithoutSeries(goodReadsBook.getTitleWithoutSeries())
            .ratingsCount(NumberUtils.toInt(goodReadsBook.getRatingsCount()))
            .reviewCount(NumberUtils.toInt(goodReadsBook.getReviewCount()));

        List<Author> authors = goodReadsBook.getAuthors();
        if (authors != null) {
            List<AuthorDto> authorDtos = authors.stream()
                .map(author -> {
                    AuthorDtoBuilder authorDtoBuilder = AuthorDto.builder()
                        .id(NumberUtils.toInt(author.getId()))
                        .name(author.getName());
                    if (author.getRole() != null) {
                        authorDtoBuilder.role(author.getRole());
                    }
                    return authorDtoBuilder.build();
                })
                .collect(Collectors.toList());
            bookDtoBuilder.authors(authorDtos);
        }
        return bookDtoBuilder.build();
    };

    public List<BookDto> find() {
        return List.of();
    }

    public Optional<BookDto> findBookByIsbn(String isbn) {
        return Optional.empty();
    }

    public Optional<BookDto> findBookByIsbn13(String isbn) {
        return Optional.empty();
    }
}
