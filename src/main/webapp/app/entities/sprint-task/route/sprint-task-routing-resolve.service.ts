import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ISprintTask } from '../sprint-task.model';
import { SprintTaskService } from '../service/sprint-task.service';

const sprintTaskResolve = (route: ActivatedRouteSnapshot): Observable<null | ISprintTask> => {
  const id = route.params.id;
  if (id) {
    return inject(SprintTaskService)
      .find(id)
      .pipe(
        mergeMap((sprintTask: HttpResponse<ISprintTask>) => {
          if (sprintTask.body) {
            return of(sprintTask.body);
          }
          inject(Router).navigate(['404']);
          return EMPTY;
        }),
      );
  }
  return of(null);
};

export default sprintTaskResolve;
