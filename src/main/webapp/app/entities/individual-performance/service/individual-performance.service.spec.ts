import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';

import { IIndividualPerformance } from '../individual-performance.model';
import {
  sampleWithFullData,
  sampleWithNewData,
  sampleWithPartialData,
  sampleWithRequiredData,
} from '../individual-performance.test-samples';

import { IndividualPerformanceService } from './individual-performance.service';

const requireRestSample: IIndividualPerformance = {
  ...sampleWithRequiredData,
};

describe('IndividualPerformance Service', () => {
  let service: IndividualPerformanceService;
  let httpMock: HttpTestingController;
  let expectedResult: IIndividualPerformance | IIndividualPerformance[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(IndividualPerformanceService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should create a IndividualPerformance', () => {
      const individualPerformance = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(individualPerformance).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a IndividualPerformance', () => {
      const individualPerformance = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(individualPerformance).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a IndividualPerformance', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of IndividualPerformance', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a IndividualPerformance', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addIndividualPerformanceToCollectionIfMissing', () => {
      it('should add a IndividualPerformance to an empty array', () => {
        const individualPerformance: IIndividualPerformance = sampleWithRequiredData;
        expectedResult = service.addIndividualPerformanceToCollectionIfMissing([], individualPerformance);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(individualPerformance);
      });

      it('should not add a IndividualPerformance to an array that contains it', () => {
        const individualPerformance: IIndividualPerformance = sampleWithRequiredData;
        const individualPerformanceCollection: IIndividualPerformance[] = [
          {
            ...individualPerformance,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addIndividualPerformanceToCollectionIfMissing(individualPerformanceCollection, individualPerformance);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a IndividualPerformance to an array that doesn't contain it", () => {
        const individualPerformance: IIndividualPerformance = sampleWithRequiredData;
        const individualPerformanceCollection: IIndividualPerformance[] = [sampleWithPartialData];
        expectedResult = service.addIndividualPerformanceToCollectionIfMissing(individualPerformanceCollection, individualPerformance);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(individualPerformance);
      });

      it('should add only unique IndividualPerformance to an array', () => {
        const individualPerformanceArray: IIndividualPerformance[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const individualPerformanceCollection: IIndividualPerformance[] = [sampleWithRequiredData];
        expectedResult = service.addIndividualPerformanceToCollectionIfMissing(
          individualPerformanceCollection,
          ...individualPerformanceArray,
        );
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const individualPerformance: IIndividualPerformance = sampleWithRequiredData;
        const individualPerformance2: IIndividualPerformance = sampleWithPartialData;
        expectedResult = service.addIndividualPerformanceToCollectionIfMissing([], individualPerformance, individualPerformance2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(individualPerformance);
        expect(expectedResult).toContain(individualPerformance2);
      });

      it('should accept null and undefined values', () => {
        const individualPerformance: IIndividualPerformance = sampleWithRequiredData;
        expectedResult = service.addIndividualPerformanceToCollectionIfMissing([], null, individualPerformance, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(individualPerformance);
      });

      it('should return initial array if no IndividualPerformance is added', () => {
        const individualPerformanceCollection: IIndividualPerformance[] = [sampleWithRequiredData];
        expectedResult = service.addIndividualPerformanceToCollectionIfMissing(individualPerformanceCollection, undefined, null);
        expect(expectedResult).toEqual(individualPerformanceCollection);
      });
    });

    describe('compareIndividualPerformance', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareIndividualPerformance(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareIndividualPerformance(entity1, entity2);
        const compareResult2 = service.compareIndividualPerformance(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareIndividualPerformance(entity1, entity2);
        const compareResult2 = service.compareIndividualPerformance(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareIndividualPerformance(entity1, entity2);
        const compareResult2 = service.compareIndividualPerformance(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
