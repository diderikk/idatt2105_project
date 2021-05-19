package idatt2105.backend.Web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import idatt2105.backend.Model.DTO.GETSectionDTO;
import idatt2105.backend.Model.DTO.SectionStatisticsDTO;
import idatt2105.backend.Service.SectionService;
import javassist.NotFoundException;

@RestController
@RequestMapping("/api/v1/sections")
public class SectionController {

    @Autowired
    private SectionService sectionService;
    
    @GetMapping("/statistics/top-sections")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<GETSectionDTO>> getTopSections(){
        return new ResponseEntity<>(sectionService.getTopSections(), HttpStatus.OK);
    }

    @GetMapping("/{section_id}/statistics")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<SectionStatisticsDTO> getStatistics(@PathVariable("section_id") long sectionId){
        try {
            return new ResponseEntity<>(sectionService.getStatistics(sectionId), HttpStatus.OK);
        } catch (NotFoundException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
