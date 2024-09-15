import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';

import { IStatusCategory } from '../status-category.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../status-category.test-samples';

import { StatusCategoryService } from './status-category.service';

const requireRestSample: IStatusCategory = {
  ...sampleWithRequiredData,
};

describe('StatusCategory Service', () => {
  let service: StatusCategoryService;
  let httpMock: HttpTestingController;
  let expectedResult: IStatusCategory | IStatusCategory[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(StatusCategoryService);
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

    it('should create a StatusCategory', () => {
      const statusCategory = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(statusCategory).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a StatusCategory', () => {
      const statusCategory = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(statusCategory).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a StatusCategory', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of StatusCategory', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a StatusCategory', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addStatusCategoryToCollectionIfMissing', () => {
      it('should add a StatusCategory to an empty array', () => {
        const statusCategory: IStatusCategory = sampleWithRequiredData;
        expectedResult = service.addStatusCategoryToCollectionIfMissing([], statusCategory);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(statusCategory);
      });

      it('should not add a StatusCategory to an array that contains it', () => {
        const statusCategory: IStatusCategory = sampleWithRequiredData;
        const statusCategoryCollection: IStatusCategory[] = [
          {
            ...statusCategory,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addStatusCategoryToCollectionIfMissing(statusCategoryCollection, statusCategory);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a StatusCategory to an array that doesn't contain it", () => {
        const statusCategory: IStatusCategory = sampleWithRequiredData;
        const statusCategoryCollection: IStatusCategory[] = [sampleWithPartialData];
        expectedResult = service.addStatusCategoryToCollectionIfMissing(statusCategoryCollection, statusCategory);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(statusCategory);
      });

      it('should add only unique StatusCategory to an array', () => {
        const statusCategoryArray: IStatusCategory[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const statusCategoryCollection: IStatusCategory[] = [sampleWithRequiredData];
        expectedResult = service.addStatusCategoryToCollectionIfMissing(statusCategoryCollection, ...statusCategoryArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const statusCategory: IStatusCategory = sampleWithRequiredData;
        const statusCategory2: IStatusCategory = sampleWithPartialData;
        expectedResult = service.addStatusCategoryToCollectionIfMissing([], statusCategory, statusCategory2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(statusCategory);
        expect(expectedResult).toContain(statusCategory2);
      });

      it('should accept null and undefined values', () => {
        const statusCategory: IStatusCategory = sampleWithRequiredData;
        expectedResult = service.addStatusCategoryToCollectionIfMissing([], null, statusCategory, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(statusCategory);
      });

      it('should return initial array if no StatusCategory is added', () => {
        const statusCategoryCollection: IStatusCategory[] = [sampleWithRequiredData];
        expectedResult = service.addStatusCategoryToCollectionIfMissing(statusCategoryCollection, undefined, null);
        expect(expectedResult).toEqual(statusCategoryCollection);
      });
    });

    describe('compareStatusCategory', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareStatusCategory(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareStatusCategory(entity1, entity2);
        const compareResult2 = service.compareStatusCategory(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareStatusCategory(entity1, entity2);
        const compareResult2 = service.compareStatusCategory(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareStatusCategory(entity1, entity2);
        const compareResult2 = service.compareStatusCategory(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
