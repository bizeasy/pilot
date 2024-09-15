import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import SprintTaskResolve from './route/sprint-task-routing-resolve.service';

const sprintTaskRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/sprint-task.component').then(m => m.SprintTaskComponent),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/sprint-task-detail.component').then(m => m.SprintTaskDetailComponent),
    resolve: {
      sprintTask: SprintTaskResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/sprint-task-update.component').then(m => m.SprintTaskUpdateComponent),
    resolve: {
      sprintTask: SprintTaskResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/sprint-task-update.component').then(m => m.SprintTaskUpdateComponent),
    resolve: {
      sprintTask: SprintTaskResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default sprintTaskRoute;
