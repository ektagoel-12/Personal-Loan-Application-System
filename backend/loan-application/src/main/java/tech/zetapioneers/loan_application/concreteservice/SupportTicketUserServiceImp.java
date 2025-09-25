package tech.zetapioneers.loan_application.concreteservice;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import tech.zetapioneers.loan_application.dto.SupportTicketDto;
import tech.zetapioneers.loan_application.entities.SupportTicket;
import tech.zetapioneers.loan_application.entities.User;
import tech.zetapioneers.loan_application.repositories.SupportTicketRepository;
import tech.zetapioneers.loan_application.repositories.UserRepository;
import tech.zetapioneers.loan_application.services.SupportTicketUserService;

import java.util.List;
@Service
public class SupportTicketUserServiceImp implements SupportTicketUserService {

    @Autowired
    SupportTicketRepository supportTicketRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    ModelMapper modelMapper;

    @Override
    public ResponseEntity<SupportTicketDto> createTicket(SupportTicketDto supportTicketDto) {
        SupportTicket supportTicket=modelMapper.map(supportTicketDto,SupportTicket.class);
        SupportTicket createdTicket=supportTicketRepository.save(supportTicket);
        return ResponseEntity.status(201).body(modelMapper.map(createdTicket,SupportTicketDto.class));
    }

    @Override
    public ResponseEntity<List<SupportTicketDto>> getTicketsByUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: "+email));
        List<SupportTicketDto> response=supportTicketRepository.findByUser(user)
                .stream().map(supportTicket -> modelMapper.map(supportTicket,SupportTicketDto.class)).toList();
        return ResponseEntity.status(200).body(response);
    }
}
