package in.beze.pilot.service;

import in.beze.pilot.domain.Comment;
import in.beze.pilot.repository.CommentRepository;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link in.beze.pilot.domain.Comment}.
 */
@Service
@Transactional
public class CommentService {

    private static final Logger LOG = LoggerFactory.getLogger(CommentService.class);

    private final CommentRepository commentRepository;

    public CommentService(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    /**
     * Save a comment.
     *
     * @param comment the entity to save.
     * @return the persisted entity.
     */
    public Comment save(Comment comment) {
        LOG.debug("Request to save Comment : {}", comment);
        return commentRepository.save(comment);
    }

    /**
     * Update a comment.
     *
     * @param comment the entity to save.
     * @return the persisted entity.
     */
    public Comment update(Comment comment) {
        LOG.debug("Request to update Comment : {}", comment);
        return commentRepository.save(comment);
    }

    /**
     * Partially update a comment.
     *
     * @param comment the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<Comment> partialUpdate(Comment comment) {
        LOG.debug("Request to partially update Comment : {}", comment);

        return commentRepository
            .findById(comment.getId())
            .map(existingComment -> {
                if (comment.getText() != null) {
                    existingComment.setText(comment.getText());
                }

                return existingComment;
            })
            .map(commentRepository::save);
    }

    /**
     * Get all the comments with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<Comment> findAllWithEagerRelationships(Pageable pageable) {
        return commentRepository.findAllWithEagerRelationships(pageable);
    }

    /**
     * Get one comment by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<Comment> findOne(Long id) {
        LOG.debug("Request to get Comment : {}", id);
        return commentRepository.findOneWithEagerRelationships(id);
    }

    /**
     * Delete the comment by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete Comment : {}", id);
        commentRepository.deleteById(id);
    }
}
