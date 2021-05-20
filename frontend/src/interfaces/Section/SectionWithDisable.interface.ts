import Section from "./Section.interface";

export default interface SectionWithDisable extends Section{
    isDisabled: boolean;
}