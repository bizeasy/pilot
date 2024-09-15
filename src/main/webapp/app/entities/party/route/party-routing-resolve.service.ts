import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IParty } from '../party.model';
import { PartyService } from '../service/party.service';

const partyResolve = (route: ActivatedRouteSnapshot): Observable<null | IParty> => {
  const id = route.params.id;
  if (id) {
    return inject(PartyService)
      .find(id)
      .pipe(
        mergeMap((party: HttpResponse<IParty>) => {
          if (party.body) {
            return of(party.body);
          }
          inject(Router).navigate(['404']);
          return EMPTY;
        }),
      );
  }
  return of(null);
};

export default partyResolve;
