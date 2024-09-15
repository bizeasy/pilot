import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IPartyType } from '../party-type.model';
import { PartyTypeService } from '../service/party-type.service';

const partyTypeResolve = (route: ActivatedRouteSnapshot): Observable<null | IPartyType> => {
  const id = route.params.id;
  if (id) {
    return inject(PartyTypeService)
      .find(id)
      .pipe(
        mergeMap((partyType: HttpResponse<IPartyType>) => {
          if (partyType.body) {
            return of(partyType.body);
          }
          inject(Router).navigate(['404']);
          return EMPTY;
        }),
      );
  }
  return of(null);
};

export default partyTypeResolve;
