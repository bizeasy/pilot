import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import RetrospectiveResolve from './route/retrospective-routing-resolve.service';

const retrospectiveRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/retrospective.component').then(m => m.RetrospectiveComponent),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/retrospective-detail.component').then(m => m.RetrospectiveDetailComponent),
    resolve: {
      retrospective: RetrospectiveResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/retrospective-update.component').then(m => m.RetrospectiveUpdateComponent),
    resolve: {
      retrospective: RetrospectiveResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/retrospective-update.component').then(m => m.RetrospectiveUpdateComponent),
    resolve: {
      retrospective: RetrospectiveResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default retrospectiveRoute;
