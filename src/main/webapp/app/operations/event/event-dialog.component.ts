import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Response } from '@angular/http';

import { Observable } from 'rxjs/Rx';
import { NgbActiveModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager, JhiAlertService } from 'ng-jhipster';

import { Event } from './event.model';
import { EventPopupService } from './event-popup.service';
import { EventService } from './event.service';

@Component({
    selector: 'jhi-event-dialog',
    templateUrl: './event-dialog.component.html'
})
export class EventDialogComponent implements OnInit, OnDestroy {

    event: Event;
    isSaving: boolean;
    startingDateDp: any;
    endingDateDp: any;

    constructor(
        public activeModal: NgbActiveModal,
        private alertService: JhiAlertService,
        private eventService: EventService,
        private eventManager: JhiEventManager
    ) { }

    ngOnInit() {
        this.isSaving = false;
    }

    ngOnDestroy() { }

    clear() {
        this.activeModal.dismiss('cancel');
    }

    save() {
        this.isSaving = true;
        if (this.event.id !== undefined) {
            this.subscribeToSaveResponse(
                this.eventService.update(this.event));
        } else {
            this.event.startingDate = this.convertDate(this.event.startingDate);
            if (this.event.endingDate) {
                this.event.endingDate = this.convertDate(this.event.endingDate);
            }
            this.eventService.create(this.event).subscribe(
                (res: any) => this.onEventSuccess(res),
                (error: Response) => this.onError(error)
            )
        }
    }

    private subscribeToSaveResponse(result: Observable<Event>) {
        result.subscribe((res: Event) =>
            this.onSaveSuccess(res), (res: Response) => this.onSaveError(res));
    }

    onEventSuccess(res: any) {
        this.isSaving = false;
        // this.eventManager.broadcast({ name: 'event_success', content: 'OK' });
        this.eventManager.broadcast({ name: 'eventListModification', content: 'OK' });
        this.clear();
    }

    private onSaveSuccess(result: Event) {
        this.eventManager.broadcast({ name: 'eventListModification', content: 'OK' });
        this.isSaving = false;
        this.activeModal.dismiss(result);
    }

    private onSaveError(error) {
        try {
            error.json();
        } catch (exception) {
            error.message = error.text();
        }
        this.isSaving = false;
        this.onError(error);
    }

    private onError(error) {
        this.alertService.error(error.message, null, null);
    }

    private convertDate(date: any) {
        let dateAux, dateMonth, dateDay;
        if (date['month'].toString().length === 1) {
            dateMonth = '0' + date['month'];
        } else {
            dateMonth = date['month'];
        }
        if (date['day'].toString().length === 1) {
            dateDay = '0' + date['day'];
        } else {
            dateDay = date['day'];
        }
        dateAux = date['year'] + '-' + dateMonth + '-' + dateDay;
        return dateAux;
    }

}

@Component({
    selector: 'jhi-event-popup',
    template: ''
})
export class EventPopupComponent implements OnInit, OnDestroy {

    routeSub: any;

    constructor(
        private route: ActivatedRoute,
        private eventPopupService: EventPopupService
    ) { }

    ngOnInit() {
        this.routeSub = this.route.params.subscribe((params) => {
            if (params['id']) {
                this.eventPopupService
                    .open(EventDialogComponent as Component, params['id']);
            } else {
                this.eventPopupService
                    .open(EventDialogComponent as Component);
            }
        });
    }

    ngOnDestroy() {
        this.routeSub.unsubscribe();
    }

}
