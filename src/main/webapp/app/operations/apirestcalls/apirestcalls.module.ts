import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';

import { DeccomSharedModule } from '../../shared';
import {
    APIRestCallsService,
    APIRestCallsComponent,
    apirestcallsRoute,
} from './';

const ENTITY_STATES = [
    ...apirestcallsRoute,
];

@NgModule({
    imports: [
        DeccomSharedModule,
        RouterModule.forRoot(ENTITY_STATES, { useHash: true })
    ],
    declarations: [
        APIRestCallsComponent,
    ],
    entryComponents: [
        APIRestCallsComponent,
    ],
    providers: [
        APIRestCallsService,
    ],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class DeccomAPIRestCallsModule {}
