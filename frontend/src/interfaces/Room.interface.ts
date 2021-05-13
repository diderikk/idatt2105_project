import Section from "./Sections/Section.interface";
import SectionForCheckBox from "./Sections/SectionForCheckBox.interface";

export default interface Room {
  roomCode: string;
  sections: Array<Section> | Array<SectionForCheckBox>;
}
