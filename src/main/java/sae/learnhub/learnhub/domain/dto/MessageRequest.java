package sae.learnhub.learnhub.domain.dto;

import lombok.Data;

@Data
public class MessageRequest {
    private Long destinataireId;
    private String contenu;
}