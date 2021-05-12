package idatt2105.backend.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import idatt2105.backend.Repository.SectionRepository;

@Service
public class SectionService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SectionService.class);

    @Autowired
    private SectionRepository sectionRepository;
    
}