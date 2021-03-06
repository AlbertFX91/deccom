import { BaseEntity } from './../../shared';
import { ExtractorItem } from '../extractor/extractor.model'

export class CV {
    constructor(
        public id?: string,
        public name?: string,
        public status?: string,
        public creationMoment?: any,
        public lastUpdate?: any,
        public controlVarEntries?: any,
        public frequency?: string,
        public extractor?: ExtractorItem,
        public value?: any
    ) {
        this.id = null;
        this.name = null;
        this.status = 'RUNNING';
        this.creationMoment = new Date();
        this.lastUpdate = new Date();
        this.controlVarEntries = [];
        this.extractor = new ExtractorItem();
        this.frequency = '';
    }
}

export class CVStyle {
    constructor(
        public defaultName?: string,
        public icon?: string,
        public backgroundColor?: string,
        public fontColor?: string
    ) {
    }
}

export class NewCV {
    constructor(
        public extractorClass?: string,
        public extractorData?: any,
        public controlVariable?: CV
    ) {
        this.extractorClass = '';
        this.extractorData = {};
        this.controlVariable = new CV();
    }
}
