import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';

import { DeccomSharedModule } from '../../shared';
import { JSONVisualizerComponent } from './jsonvisualizer';

import {
    RestCallsService,
    RestCallsComponent,
    RestCallsShowComponent,
    restcallsRoute
} from './';

const ENTITY_STATES = [
    ...restcallsRoute,
];

@NgModule({
    imports: [
        DeccomSharedModule,
        RouterModule.forRoot(ENTITY_STATES, { useHash: true })
    ],
    declarations: [
        RestCallsComponent,
        RestCallsShowComponent,
        JSONVisualizerComponent
    ],
    entryComponents: [
        RestCallsComponent
    ],
    providers: [
        RestCallsService
    ],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class DeccomRestCallsModule { }