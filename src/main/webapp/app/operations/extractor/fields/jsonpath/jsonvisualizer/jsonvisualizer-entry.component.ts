import { Component, OnInit, OnDestroy, Input, Output, EventEmitter, SimpleChanges  } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Subscription } from 'rxjs/Rx';
import { JhiEventManager, JhiParseLinks, JhiPaginationUtil, JhiLanguageService, JhiAlertService } from 'ng-jhipster';

import { ITEMS_PER_PAGE, Principal, ResponseWrapper } from '../../../../../shared';
import { PaginationConfig } from '../../../../../blocks/config/uib-pagination.config';

@Component({
    selector: 'jhi-jsonvisualizer-entry',
    templateUrl: './jsonvisualizer-entry.component.html',
    styleUrls: [
        'jsonvisualizer-entry.css'
    ],
}) export class JSONVisualizerEntryComponent implements OnInit, OnDestroy  {

    @Input()
    data: any;

    @Output()
    private selected = new EventEmitter<String>();

    @Output()
    private correctPath = new EventEmitter<boolean>();

    @Input()
    parentType: string;

    states: any;

    key_style = {
        'number': ['fa', 'fa-circle', 'number'],
        'string': ['fa', 'fa-circle', 'string'],
        'boolean': ['fa', 'fa-circle', 'boolean'],
        'array': ['brackets', 'img-fluid'],
        'json': ['keys', 'img-fluid']
    };

    currentKey: string;

    constructor() {
        this.states = {};
        this.currentKey = null;
     }

    ngOnInit() { }

    ngOnDestroy() { }

    keys() {
        return Object.keys(this.data);
    }

    typeByKey(key: string) {
        // Value of key
        const value = this.data[key];
        // Type of value
        const type = typeof (value);
        // Get type if value is not an array, dict or null (string, number or boolean)
        if (type !== 'object' && type !== 'undefined') {
            if (!isNaN(value)) {
                return 'number';
            } else {
                return type;
            }
        }
        // Get type of value when value is array
        if (Array.isArray(value)) {
            return 'array';
        }
        // Get type of value when value is an object and not null or array (JSON)
        if (type === 'object' && value !== null) {
            return 'json';
        }
    }

    styleByKey(key: string) {
        return this.key_style[this.typeByKey(key)];
    }

    showValue(key: string) {
        const type = this.typeByKey(key);
        return type !== 'array' && type !== 'json';
    }

    valueByKey(key: string) {
        const value = this.data[key];
        if (this.typeByKey(key) === 'string') {
            return '"' + value + '"';
        }
        return value;
    }

    fireSelected(path: string, key: string) {
        this.currentKey = null;
        if (this.parentType === 'array') {
            this.selected.emit('[' + key + '].' + path);
        } else {
            this.selected.emit(key + '.' + path);
        }
    }

    rowClicked(key: string) {
        this.currentKey = key;
        if (this.isSelected(key)) {
            this.correctPath.emit(true);
        } else {
            this.correctPath.emit(false);
        }
        this.toggleVisualization(key);
        this.startPath(key);
    }

    startPath(key: string) {
        if (this.parentType === 'array') {
            this.selected.emit('[' + key + ']');
        } else {
            this.selected.emit(key);
        }
    }

    toggleVisualization(key: string) {
        const type = this.typeByKey(key);
        if (type === 'json' || type === 'array') {
            if (this.states[key]) {
                this.states[key] = this.states[key] === 'out' ? 'in' : 'out';
            } else {
                this.states[key] = 'in';
            }
        }
    }

    cantBeVisualized(key: string) {
        const type = this.typeByKey(key);
        // It can be visualized if its a json or and array, and if the object has been setted to visible because it has been clicked
        return (type === 'json' || type === 'array') && this.states[key] === 'in';
    }

    isSelected(key: string) {
        if (key !== this.currentKey) {
            return false;
        }
        const value = this.data[key];
        let type = typeof (value);

        if (!isNaN(value)) {
            type = 'number';
        }

        return type === 'number';
    }

    onCorrectPath(e) {
        if (e) {
            this.currentKey = null;
            this.correctPath.emit(true);
        } else {
            this.correctPath.emit(false);
        }
    }
}
