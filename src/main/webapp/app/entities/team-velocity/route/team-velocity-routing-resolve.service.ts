import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ITeamVelocity } from '../team-velocity.model';
import { TeamVelocityService } from '../service/team-velocity.service';

const teamVelocityResolve = (route: ActivatedRouteSnapshot): Observable<null | ITeamVelocity> => {
  const id = route.params.id;
  if (id) {
    return inject(TeamVelocityService)
      .find(id)
      .pipe(
        mergeMap((teamVelocity: HttpResponse<ITeamVelocity>) => {
          if (teamVelocity.body) {
            return of(teamVelocity.body);
          }
          inject(Router).navigate(['404']);
          return EMPTY;
        }),
      );
  }
  return of(null);
};

export default teamVelocityResolve;
