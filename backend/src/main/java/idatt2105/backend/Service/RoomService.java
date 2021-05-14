package idatt2105.backend.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import idatt2105.backend.Repository.RoomRepository;

@Service
public class RoomService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RoomService.class);

    @Autowired
    private RoomRepository roomRepository;

}