import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IStatusCategory } from '../status-category.model';
import { StatusCategoryService } from '../service/status-category.service';

const statusCategoryResolve = (route: ActivatedRouteSnapshot): Observable<null | IStatusCategory> => {
  const id = route.params.id;
  if (id) {
    return inject(StatusCategoryService)
      .find(id)
      .pipe(
        mergeMap((statusCategory: HttpResponse<IStatusCategory>) => {
          if (statusCategory.body) {
            return of(statusCategory.body);
          }
          inject(Router).navigate(['404']);
          return EMPTY;
        }),
      );
  }
  return of(null);
};

export default statusCategoryResolve;
