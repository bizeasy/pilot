import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import StatusCategoryResolve from './route/status-category-routing-resolve.service';

const statusCategoryRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/status-category.component').then(m => m.StatusCategoryComponent),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/status-category-detail.component').then(m => m.StatusCategoryDetailComponent),
    resolve: {
      statusCategory: StatusCategoryResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/status-category-update.component').then(m => m.StatusCategoryUpdateComponent),
    resolve: {
      statusCategory: StatusCategoryResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/status-category-update.component').then(m => m.StatusCategoryUpdateComponent),
    resolve: {
      statusCategory: StatusCategoryResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default statusCategoryRoute;
