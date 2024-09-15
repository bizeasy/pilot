import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IStatusCategory, NewStatusCategory } from '../status-category.model';

export type PartialUpdateStatusCategory = Partial<IStatusCategory> & Pick<IStatusCategory, 'id'>;

export type EntityResponseType = HttpResponse<IStatusCategory>;
export type EntityArrayResponseType = HttpResponse<IStatusCategory[]>;

@Injectable({ providedIn: 'root' })
export class StatusCategoryService {
  protected http = inject(HttpClient);
  protected applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/status-categories');

  create(statusCategory: NewStatusCategory): Observable<EntityResponseType> {
    return this.http.post<IStatusCategory>(this.resourceUrl, statusCategory, { observe: 'response' });
  }

  update(statusCategory: IStatusCategory): Observable<EntityResponseType> {
    return this.http.put<IStatusCategory>(`${this.resourceUrl}/${this.getStatusCategoryIdentifier(statusCategory)}`, statusCategory, {
      observe: 'response',
    });
  }

  partialUpdate(statusCategory: PartialUpdateStatusCategory): Observable<EntityResponseType> {
    return this.http.patch<IStatusCategory>(`${this.resourceUrl}/${this.getStatusCategoryIdentifier(statusCategory)}`, statusCategory, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IStatusCategory>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IStatusCategory[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getStatusCategoryIdentifier(statusCategory: Pick<IStatusCategory, 'id'>): number {
    return statusCategory.id;
  }

  compareStatusCategory(o1: Pick<IStatusCategory, 'id'> | null, o2: Pick<IStatusCategory, 'id'> | null): boolean {
    return o1 && o2 ? this.getStatusCategoryIdentifier(o1) === this.getStatusCategoryIdentifier(o2) : o1 === o2;
  }

  addStatusCategoryToCollectionIfMissing<Type extends Pick<IStatusCategory, 'id'>>(
    statusCategoryCollection: Type[],
    ...statusCategoriesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const statusCategories: Type[] = statusCategoriesToCheck.filter(isPresent);
    if (statusCategories.length > 0) {
      const statusCategoryCollectionIdentifiers = statusCategoryCollection.map(statusCategoryItem =>
        this.getStatusCategoryIdentifier(statusCategoryItem),
      );
      const statusCategoriesToAdd = statusCategories.filter(statusCategoryItem => {
        const statusCategoryIdentifier = this.getStatusCategoryIdentifier(statusCategoryItem);
        if (statusCategoryCollectionIdentifiers.includes(statusCategoryIdentifier)) {
          return false;
        }
        statusCategoryCollectionIdentifiers.push(statusCategoryIdentifier);
        return true;
      });
      return [...statusCategoriesToAdd, ...statusCategoryCollection];
    }
    return statusCategoryCollection;
  }
}
