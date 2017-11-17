import { Injectable } from '@angular/core';
import { Http, Response, URLSearchParams, BaseRequestOptions } from '@angular/http';
import { Observable } from 'rxjs/Rx';
import { ResponseWrapper, createRequestOption } from '../../shared';
import { CVCard } from './cv.model';

@Injectable()
export class CVService {

    private resourceUrl = 'api/cv/';

    cvCards: CVCard[];

    constructor(private http: Http) {
        this.cvCards = [
            {
                'id': 'cvCardId1',
                'logo_url': 'http://www.iconhot.com/icon/png/coded/512/sql-1.png',
                'name': 'Followers',
                'value': '160',
                'entries': 1,
                'creation_moment': '02/02/2017 8:15',
                'last_update': '05/05/2017 10:30',
                'status': 'Running'
            },
            {
                'id': 'cvCardId2',
                'logo_url': 'https://www.vera.com/wp-content/uploads/2016/04/apps-sdk_03_rest_api_275.png',
                'name': 'Following',
                'value': '100',
                'entries': 1,
                'creation_moment': '01/01/2016 10:15',
                'last_update': '05/05/2017 11:30',
                'status': 'Running'
            },
            {
                'id': 'cvCardId3',
                'logo_url': 'https://www.vera.com/wp-content/uploads/2016/04/apps-sdk_03_rest_api_275.png',
                'name': 'Friends',
                'value': '10',
                'entries': 1,
                'creation_moment': '10/10/2016 8:45',
                'last_update': '11/11/2016 12:30',
                'status': 'Paused'

            },
            {
                'id': 'cvCardId4',
                'logo_url': 'http://www.freeiconspng.com/uploads/images-facebook-f-logo-png-transparent-background-page-2-29.png',
                'name': 'Contacts',
                'value': '40',
                'entries': 3,
                'creation_moment': '02/02/2014 8:15',
                'last_update': '12/12/2015 9:30',
                'status': 'Blocked'
            }
        ]
    }

    getCards() {
        return this.cvCards;
    }

    getRunningCards() {
        return this.filterCardsByStatus('Running');
    }

    getPausedCards() {
        return this.filterCardsByStatus('Paused');
    }

    getBlockedCards() {
        return this.filterCardsByStatus('Blocked');
    }

    filterCardsByStatus(status: String) {
        return this.cvCards.filter((cvCard) => cvCard.status === status);
    }

}
