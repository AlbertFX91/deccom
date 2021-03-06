
import { Injectable } from '@angular/core';
import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot, Routes, CanActivate } from '@angular/router';

import { UserRouteAccessService } from '../../shared';
import { JhiPaginationUtil } from 'ng-jhipster';

import { EventComponent } from './event.component';
import { EventPopupComponent } from './event-dialog.component';
import { EventDeletePopupComponent } from './event-delete-dialog.component';

export const eventRoute: Routes = [
    {
        path: 'event',
        component: EventComponent,
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'deccomApp.event.home.title'
        },
        canActivate: [UserRouteAccessService]
    }
];

export const eventPopupRoute: Routes = [
    {
        path: 'event-new',
        component: EventPopupComponent,
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'deccomApp.event.home.title'
        },
        canActivate: [UserRouteAccessService],
        outlet: 'popup'
    },
    {
        path: 'event/:id/edit',
        component: EventPopupComponent,
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'deccomApp.event.home.title'
        },
        canActivate: [UserRouteAccessService],
        outlet: 'popup'
    },
    {
        path: 'event/:id/delete',
        component: EventDeletePopupComponent,
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'deccomApp.event.home.title'
        },
        canActivate: [UserRouteAccessService],
        outlet: 'popup'
    }
];
