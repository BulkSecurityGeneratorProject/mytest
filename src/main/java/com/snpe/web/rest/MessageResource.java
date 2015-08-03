package com.snpe.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.snpe.domain.Message;
import com.snpe.repository.MessageRepository;
import com.snpe.web.rest.util.HeaderUtil;
import com.snpe.web.rest.util.PaginationUtil;
import com.snpe.web.rest.dto.MessageDTO;
import com.snpe.web.rest.mapper.MessageMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * REST controller for managing Message.
 */
@RestController
@RequestMapping("/api")
public class MessageResource {

    private final Logger log = LoggerFactory.getLogger(MessageResource.class);

    @Inject
    private MessageRepository messageRepository;

    @Inject
    private MessageMapper messageMapper;

    /**
     * POST  /messages -> Create a new message.
     */
    @RequestMapping(value = "/messages",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<MessageDTO> create(@RequestBody MessageDTO messageDTO) throws URISyntaxException {
        log.debug("REST request to save Message : {}", messageDTO);
        if (messageDTO.getId() != null) {
            return ResponseEntity.badRequest().header("Failure", "A new message cannot already have an ID").body(null);
        }
        Message message = messageMapper.messageDTOToMessage(messageDTO);
        Message result = messageRepository.save(message);
        return ResponseEntity.created(new URI("/api/messages/" + result.getId()))
                .headers(HeaderUtil.createEntityCreationAlert("message", result.getId().toString()))
                .body(messageMapper.messageToMessageDTO(result));
    }

    /**
     * PUT  /messages -> Updates an existing message.
     */
    @RequestMapping(value = "/messages",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<MessageDTO> update(@RequestBody MessageDTO messageDTO) throws URISyntaxException {
        log.debug("REST request to update Message : {}", messageDTO);
        if (messageDTO.getId() == null) {
            return create(messageDTO);
        }
        Message message = messageMapper.messageDTOToMessage(messageDTO);
        Message result = messageRepository.save(message);
        return ResponseEntity.ok()
                .headers(HeaderUtil.createEntityUpdateAlert("message", messageDTO.getId().toString()))
                .body(messageMapper.messageToMessageDTO(result));
    }

    /**
     * GET  /messages -> get all the messages.
     */
    @RequestMapping(value = "/messages",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @Transactional(readOnly = true)
    public ResponseEntity<List<MessageDTO>> getAll(@RequestParam(value = "page" , required = false) Integer offset,
                                  @RequestParam(value = "per_page", required = false) Integer limit)
        throws URISyntaxException {
        Page<Message> page = messageRepository.findAll(PaginationUtil.generatePageRequest(offset, limit));
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/messages", offset, limit);
        return new ResponseEntity<>(page.getContent().stream()
            .map(messageMapper::messageToMessageDTO)
            .collect(Collectors.toCollection(LinkedList::new)), headers, HttpStatus.OK);
    }

    /**
     * GET  /messages/:id -> get the "id" message.
     */
    @RequestMapping(value = "/messages/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<MessageDTO> get(@PathVariable Long id) {
        log.debug("REST request to get Message : {}", id);
        return Optional.ofNullable(messageRepository.findOne(id))
            .map(messageMapper::messageToMessageDTO)
            .map(messageDTO -> new ResponseEntity<>(
                messageDTO,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /messages/:id -> delete the "id" message.
     */
    @RequestMapping(value = "/messages/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.debug("REST request to delete Message : {}", id);
        messageRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("message", id.toString())).build();
    }
}
