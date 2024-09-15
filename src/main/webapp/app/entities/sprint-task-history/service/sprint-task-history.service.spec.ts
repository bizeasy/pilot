import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';

import { DATE_FORMAT } from 'app/config/input.constants';
import { ISprintTaskHistory } from '../sprint-task-history.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../sprint-task-history.test-samples';

import { RestSprintTaskHistory, SprintTaskHistoryService } from './sprint-task-history.service';

const requireRestSample: RestSprintTaskHistory = {
  ...sampleWithRequiredData,
  fromDate: sampleWithRequiredData.fromDate?.format(DATE_FORMAT),
  toDate: sampleWithRequiredData.toDate?.format(DATE_FORMAT),
};

describe('SprintTaskHistory Service', () => {
  let service: SprintTaskHistoryService;
  let httpMock: HttpTestingController;
  let expectedResult: ISprintTaskHistory | ISprintTaskHistory[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(SprintTaskHistoryService);
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

    it('should create a SprintTaskHistory', () => {
      const sprintTaskHistory = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(sprintTaskHistory).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a SprintTaskHistory', () => {
      const sprintTaskHistory = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(sprintTaskHistory).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a SprintTaskHistory', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of SprintTaskHistory', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a SprintTaskHistory', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addSprintTaskHistoryToCollectionIfMissing', () => {
      it('should add a SprintTaskHistory to an empty array', () => {
        const sprintTaskHistory: ISprintTaskHistory = sampleWithRequiredData;
        expectedResult = service.addSprintTaskHistoryToCollectionIfMissing([], sprintTaskHistory);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(sprintTaskHistory);
      });

      it('should not add a SprintTaskHistory to an array that contains it', () => {
        const sprintTaskHistory: ISprintTaskHistory = sampleWithRequiredData;
        const sprintTaskHistoryCollection: ISprintTaskHistory[] = [
          {
            ...sprintTaskHistory,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addSprintTaskHistoryToCollectionIfMissing(sprintTaskHistoryCollection, sprintTaskHistory);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a SprintTaskHistory to an array that doesn't contain it", () => {
        const sprintTaskHistory: ISprintTaskHistory = sampleWithRequiredData;
        const sprintTaskHistoryCollection: ISprintTaskHistory[] = [sampleWithPartialData];
        expectedResult = service.addSprintTaskHistoryToCollectionIfMissing(sprintTaskHistoryCollection, sprintTaskHistory);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(sprintTaskHistory);
      });

      it('should add only unique SprintTaskHistory to an array', () => {
        const sprintTaskHistoryArray: ISprintTaskHistory[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const sprintTaskHistoryCollection: ISprintTaskHistory[] = [sampleWithRequiredData];
        expectedResult = service.addSprintTaskHistoryToCollectionIfMissing(sprintTaskHistoryCollection, ...sprintTaskHistoryArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const sprintTaskHistory: ISprintTaskHistory = sampleWithRequiredData;
        const sprintTaskHistory2: ISprintTaskHistory = sampleWithPartialData;
        expectedResult = service.addSprintTaskHistoryToCollectionIfMissing([], sprintTaskHistory, sprintTaskHistory2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(sprintTaskHistory);
        expect(expectedResult).toContain(sprintTaskHistory2);
      });

      it('should accept null and undefined values', () => {
        const sprintTaskHistory: ISprintTaskHistory = sampleWithRequiredData;
        expectedResult = service.addSprintTaskHistoryToCollectionIfMissing([], null, sprintTaskHistory, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(sprintTaskHistory);
      });

      it('should return initial array if no SprintTaskHistory is added', () => {
        const sprintTaskHistoryCollection: ISprintTaskHistory[] = [sampleWithRequiredData];
        expectedResult = service.addSprintTaskHistoryToCollectionIfMissing(sprintTaskHistoryCollection, undefined, null);
        expect(expectedResult).toEqual(sprintTaskHistoryCollection);
      });
    });

    describe('compareSprintTaskHistory', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareSprintTaskHistory(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareSprintTaskHistory(entity1, entity2);
        const compareResult2 = service.compareSprintTaskHistory(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareSprintTaskHistory(entity1, entity2);
        const compareResult2 = service.compareSprintTaskHistory(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareSprintTaskHistory(entity1, entity2);
        const compareResult2 = service.compareSprintTaskHistory(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
