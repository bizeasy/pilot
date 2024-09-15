import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';

import { ITaskLog } from '../task-log.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../task-log.test-samples';

import { RestTaskLog, TaskLogService } from './task-log.service';

const requireRestSample: RestTaskLog = {
  ...sampleWithRequiredData,
  fromTime: sampleWithRequiredData.fromTime?.toJSON(),
  toTime: sampleWithRequiredData.toTime?.toJSON(),
};

describe('TaskLog Service', () => {
  let service: TaskLogService;
  let httpMock: HttpTestingController;
  let expectedResult: ITaskLog | ITaskLog[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(TaskLogService);
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

    it('should create a TaskLog', () => {
      const taskLog = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(taskLog).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a TaskLog', () => {
      const taskLog = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(taskLog).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a TaskLog', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of TaskLog', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a TaskLog', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addTaskLogToCollectionIfMissing', () => {
      it('should add a TaskLog to an empty array', () => {
        const taskLog: ITaskLog = sampleWithRequiredData;
        expectedResult = service.addTaskLogToCollectionIfMissing([], taskLog);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(taskLog);
      });

      it('should not add a TaskLog to an array that contains it', () => {
        const taskLog: ITaskLog = sampleWithRequiredData;
        const taskLogCollection: ITaskLog[] = [
          {
            ...taskLog,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addTaskLogToCollectionIfMissing(taskLogCollection, taskLog);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a TaskLog to an array that doesn't contain it", () => {
        const taskLog: ITaskLog = sampleWithRequiredData;
        const taskLogCollection: ITaskLog[] = [sampleWithPartialData];
        expectedResult = service.addTaskLogToCollectionIfMissing(taskLogCollection, taskLog);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(taskLog);
      });

      it('should add only unique TaskLog to an array', () => {
        const taskLogArray: ITaskLog[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const taskLogCollection: ITaskLog[] = [sampleWithRequiredData];
        expectedResult = service.addTaskLogToCollectionIfMissing(taskLogCollection, ...taskLogArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const taskLog: ITaskLog = sampleWithRequiredData;
        const taskLog2: ITaskLog = sampleWithPartialData;
        expectedResult = service.addTaskLogToCollectionIfMissing([], taskLog, taskLog2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(taskLog);
        expect(expectedResult).toContain(taskLog2);
      });

      it('should accept null and undefined values', () => {
        const taskLog: ITaskLog = sampleWithRequiredData;
        expectedResult = service.addTaskLogToCollectionIfMissing([], null, taskLog, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(taskLog);
      });

      it('should return initial array if no TaskLog is added', () => {
        const taskLogCollection: ITaskLog[] = [sampleWithRequiredData];
        expectedResult = service.addTaskLogToCollectionIfMissing(taskLogCollection, undefined, null);
        expect(expectedResult).toEqual(taskLogCollection);
      });
    });

    describe('compareTaskLog', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareTaskLog(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareTaskLog(entity1, entity2);
        const compareResult2 = service.compareTaskLog(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareTaskLog(entity1, entity2);
        const compareResult2 = service.compareTaskLog(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareTaskLog(entity1, entity2);
        const compareResult2 = service.compareTaskLog(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
