import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import IndividualPerformanceResolve from './route/individual-performance-routing-resolve.service';

const individualPerformanceRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/individual-performance.component').then(m => m.IndividualPerformanceComponent),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/individual-performance-detail.component').then(m => m.IndividualPerformanceDetailComponent),
    resolve: {
      individualPerformance: IndividualPerformanceResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/individual-performance-update.component').then(m => m.IndividualPerformanceUpdateComponent),
    resolve: {
      individualPerformance: IndividualPerformanceResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/individual-performance-update.component').then(m => m.IndividualPerformanceUpdateComponent),
    resolve: {
      individualPerformance: IndividualPerformanceResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default individualPerformanceRoute;
