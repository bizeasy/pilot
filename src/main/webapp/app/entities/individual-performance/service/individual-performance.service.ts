import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IIndividualPerformance, NewIndividualPerformance } from '../individual-performance.model';

export type PartialUpdateIndividualPerformance = Partial<IIndividualPerformance> & Pick<IIndividualPerformance, 'id'>;

export type EntityResponseType = HttpResponse<IIndividualPerformance>;
export type EntityArrayResponseType = HttpResponse<IIndividualPerformance[]>;

@Injectable({ providedIn: 'root' })
export class IndividualPerformanceService {
  protected http = inject(HttpClient);
  protected applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/individual-performances');

  create(individualPerformance: NewIndividualPerformance): Observable<EntityResponseType> {
    return this.http.post<IIndividualPerformance>(this.resourceUrl, individualPerformance, { observe: 'response' });
  }

  update(individualPerformance: IIndividualPerformance): Observable<EntityResponseType> {
    return this.http.put<IIndividualPerformance>(
      `${this.resourceUrl}/${this.getIndividualPerformanceIdentifier(individualPerformance)}`,
      individualPerformance,
      { observe: 'response' },
    );
  }

  partialUpdate(individualPerformance: PartialUpdateIndividualPerformance): Observable<EntityResponseType> {
    return this.http.patch<IIndividualPerformance>(
      `${this.resourceUrl}/${this.getIndividualPerformanceIdentifier(individualPerformance)}`,
      individualPerformance,
      { observe: 'response' },
    );
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IIndividualPerformance>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IIndividualPerformance[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getIndividualPerformanceIdentifier(individualPerformance: Pick<IIndividualPerformance, 'id'>): number {
    return individualPerformance.id;
  }

  compareIndividualPerformance(o1: Pick<IIndividualPerformance, 'id'> | null, o2: Pick<IIndividualPerformance, 'id'> | null): boolean {
    return o1 && o2 ? this.getIndividualPerformanceIdentifier(o1) === this.getIndividualPerformanceIdentifier(o2) : o1 === o2;
  }

  addIndividualPerformanceToCollectionIfMissing<Type extends Pick<IIndividualPerformance, 'id'>>(
    individualPerformanceCollection: Type[],
    ...individualPerformancesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const individualPerformances: Type[] = individualPerformancesToCheck.filter(isPresent);
    if (individualPerformances.length > 0) {
      const individualPerformanceCollectionIdentifiers = individualPerformanceCollection.map(individualPerformanceItem =>
        this.getIndividualPerformanceIdentifier(individualPerformanceItem),
      );
      const individualPerformancesToAdd = individualPerformances.filter(individualPerformanceItem => {
        const individualPerformanceIdentifier = this.getIndividualPerformanceIdentifier(individualPerformanceItem);
        if (individualPerformanceCollectionIdentifiers.includes(individualPerformanceIdentifier)) {
          return false;
        }
        individualPerformanceCollectionIdentifiers.push(individualPerformanceIdentifier);
        return true;
      });
      return [...individualPerformancesToAdd, ...individualPerformanceCollection];
    }
    return individualPerformanceCollection;
  }
}
