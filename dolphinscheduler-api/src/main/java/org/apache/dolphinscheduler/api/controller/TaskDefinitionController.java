/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.dolphinscheduler.api.controller;

import static org.apache.dolphinscheduler.api.enums.Status.CREATE_TASK_DEFINITION;
import static org.apache.dolphinscheduler.api.enums.Status.DELETE_TASK_DEFINE_BY_CODE_ERROR;
import static org.apache.dolphinscheduler.api.enums.Status.DELETE_TASK_DEFINITION_VERSION_ERROR;
import static org.apache.dolphinscheduler.api.enums.Status.QUERY_DETAIL_OF_TASK_DEFINITION_ERROR;
import static org.apache.dolphinscheduler.api.enums.Status.QUERY_TASK_DEFINITION_LIST_PAGING_ERROR;
import static org.apache.dolphinscheduler.api.enums.Status.QUERY_TASK_DEFINITION_VERSIONS_ERROR;
import static org.apache.dolphinscheduler.api.enums.Status.SWITCH_TASK_DEFINITION_VERSION_ERROR;
import static org.apache.dolphinscheduler.api.enums.Status.UPDATE_TASK_DEFINITION_ERROR;

import org.apache.dolphinscheduler.api.aspect.AccessLogAnnotation;
import org.apache.dolphinscheduler.api.enums.Status;
import org.apache.dolphinscheduler.api.exceptions.ApiException;
import org.apache.dolphinscheduler.api.service.TaskDefinitionService;
import org.apache.dolphinscheduler.api.utils.Result;
import org.apache.dolphinscheduler.common.Constants;
import org.apache.dolphinscheduler.common.utils.ParameterUtils;
import org.apache.dolphinscheduler.dao.entity.User;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import springfox.documentation.annotations.ApiIgnore;

/**
 * task definition controller
 */
@Api(tags = "TASK_DEFINITION_TAG")
@RestController
@RequestMapping("projects/{projectName}/task")
public class TaskDefinitionController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(TaskDefinitionController.class);

    @Autowired
    private TaskDefinitionService taskDefinitionService;

    /**
     * create task definition
     *
     * @param loginUser login user
     * @param projectName project name
     * @param taskDefinitionJson task definition json
     * @return create result code
     */
    @ApiOperation(value = "save", notes = "CREATE_TASK_DEFINITION_NOTES")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "projectName", value = "PROJECT_NAME", required = true, type = "String"),
            @ApiImplicitParam(name = "taskDefinitionJson", value = "TASK_DEFINITION_JSON", required = true, type = "String")
    })
    @PostMapping(value = "/save")
    @ResponseStatus(HttpStatus.CREATED)
    @ApiException(CREATE_TASK_DEFINITION)
    @AccessLogAnnotation(ignoreRequestArgs = "loginUser")
    public Result createTaskDefinition(@ApiIgnore @RequestAttribute(value = Constants.SESSION_USER) User loginUser,
                                       @ApiParam(name = "projectName", value = "PROJECT_NAME", required = true) @PathVariable String projectName,
                                       @RequestParam(value = "taskDefinitionJson", required = true) String taskDefinitionJson) {

        Map<String, Object> result = taskDefinitionService.createTaskDefinition(loginUser, projectName, taskDefinitionJson);
        return returnDataList(result);
    }

    /**
     * update task definition
     *
     * @param loginUser login user
     * @param projectName project name
     * @param taskDefinitionCode task definition code
     * @param taskDefinitionJson task definition json
     * @return update result code
     */
    @ApiOperation(value = "update", notes = "UPDATE_TASK_DEFINITION_NOTES")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "projectName", value = "PROJECT_NAME", required = true, type = "String"),
            @ApiImplicitParam(name = "code", value = "TASK_DEFINITION_CODE", required = true, dataType = "Long", example = "1"),
            @ApiImplicitParam(name = "taskDefinitionJson", value = "TASK_DEFINITION_JSON", required = true, type = "String")
    })
    @PostMapping(value = "/update")
    @ResponseStatus(HttpStatus.OK)
    @ApiException(UPDATE_TASK_DEFINITION_ERROR)
    @AccessLogAnnotation(ignoreRequestArgs = "loginUser")
    public Result updateTaskDefinition(@ApiIgnore @RequestAttribute(value = Constants.SESSION_USER) User loginUser,
                                       @ApiParam(name = "projectName", value = "PROJECT_NAME", required = true) @PathVariable String projectName,
                                       @RequestParam(value = "taskDefinitionCode") long taskDefinitionCode,
                                       @RequestParam(value = "taskDefinitionJson", required = true) String taskDefinitionJson) {
        Map<String, Object> result = taskDefinitionService.updateTaskDefinition(loginUser, projectName, taskDefinitionCode, taskDefinitionJson);
        return returnDataList(result);
    }

    /**
     * query task definition version paging list info
     *
     * @param loginUser login user info
     * @param projectName project name
     * @param pageNo the task definition version list current page number
     * @param pageSize the task definition version list page size
     * @param taskDefinitionCode the task definition code
     * @return the task definition version list
     */
    @ApiOperation(value = "queryVersions", notes = "QUERY_TASK_DEFINITION_VERSIONS_NOTES")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNo", value = "PAGE_NO", required = true, dataType = "Int", example = "100"),
            @ApiImplicitParam(name = "pageSize", value = "PAGE_SIZE", required = true, dataType = "Int", example = "100"),
            @ApiImplicitParam(name = "taskDefinitionCode", value = "TASK_DEFINITION_CODE", required = true, dataType = "Long", example = "1")
    })
    @GetMapping(value = "/versions")
    @ResponseStatus(HttpStatus.OK)
    @ApiException(QUERY_TASK_DEFINITION_VERSIONS_ERROR)
    @AccessLogAnnotation(ignoreRequestArgs = "loginUser")
    public Result queryTaskDefinitionVersions(@ApiIgnore @RequestAttribute(value = Constants.SESSION_USER) User loginUser,
                                              @ApiParam(name = "projectName", value = "PROJECT_NAME", required = true) @PathVariable String projectName,
                                              @RequestParam(value = "pageNo") int pageNo,
                                              @RequestParam(value = "pageSize") int pageSize,
                                              @RequestParam(value = "taskDefinitionCode") long taskDefinitionCode) {
        Map<String, Object> result = taskDefinitionService.queryTaskDefinitionVersions(loginUser,
                projectName, pageNo, pageSize, taskDefinitionCode);
        return returnDataList(result);
    }

    /**
     * switch task definition version
     *
     * @param loginUser login user info
     * @param projectName project name
     * @param taskDefinitionCode the task definition code
     * @param version the version user want to switch
     * @return switch version result code
     */
    @ApiOperation(value = "switchVersion", notes = "SWITCH_TASK_DEFINITION_VERSION_NOTES")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "taskDefinitionCode", value = "TASK_DEFINITION_CODE", required = true, dataType = "Long", example = "1"),
            @ApiImplicitParam(name = "version", value = "VERSION", required = true, dataType = "Int", example = "100")
    })
    @GetMapping(value = "/version/switch")
    @ResponseStatus(HttpStatus.OK)
    @ApiException(SWITCH_TASK_DEFINITION_VERSION_ERROR)
    @AccessLogAnnotation(ignoreRequestArgs = "loginUser")
    public Result switchTaskDefinitionVersion(@ApiIgnore @RequestAttribute(value = Constants.SESSION_USER) User loginUser,
                                              @ApiParam(name = "projectName", value = "PROJECT_NAME", required = true) @PathVariable String projectName,
                                              @RequestParam(value = "taskDefinitionCode") long taskDefinitionCode,
                                              @RequestParam(value = "version") int version) {
        Map<String, Object> result = taskDefinitionService.switchVersion(loginUser, projectName, taskDefinitionCode, version);
        return returnDataList(result);
    }

    /**
     * delete the certain task definition version by version and code
     *
     * @param loginUser login user info
     * @param projectName the task definition project name
     * @param taskDefinitionCode the task definition code
     * @param version the task definition version user want to delete
     * @return delete version result code
     */
    @ApiOperation(value = "deleteVersion", notes = "DELETE_TASK_DEFINITION_VERSION_NOTES")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "taskDefinitionCode", value = "TASK_DEFINITION_CODE", required = true, dataType = "Long", example = "1"),
            @ApiImplicitParam(name = "version", value = "VERSION", required = true, dataType = "Int", example = "100")
    })
    @GetMapping(value = "/version/delete")
    @ResponseStatus(HttpStatus.OK)
    @ApiException(DELETE_TASK_DEFINITION_VERSION_ERROR)
    @AccessLogAnnotation(ignoreRequestArgs = "loginUser")
    public Result deleteTaskDefinitionVersion(@ApiIgnore @RequestAttribute(value = Constants.SESSION_USER) User loginUser,
                                              @ApiParam(name = "projectName", value = "PROJECT_NAME", required = true) @PathVariable String projectName,
                                              @RequestParam(value = "taskDefinitionCode") long taskDefinitionCode,
                                              @RequestParam(value = "version") int version) {
        Map<String, Object> result = taskDefinitionService.deleteByCodeAndVersion(loginUser, projectName, taskDefinitionCode, version);
        return returnDataList(result);
    }

    /**
     * delete task definition by code
     *
     * @param loginUser login user
     * @param projectName project name
     * @param taskDefinitionCode the task definition code
     * @return delete result code
     */
    @ApiOperation(value = "deleteTaskDefinition", notes = "DELETE_TASK_DEFINITION_BY_CODE_NOTES")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "taskDefinitionCode", value = "TASK_DEFINITION_CODE", required = true, dataType = "Long", example = "1")
    })
    @GetMapping(value = "/delete")
    @ResponseStatus(HttpStatus.OK)
    @ApiException(DELETE_TASK_DEFINE_BY_CODE_ERROR)
    @AccessLogAnnotation(ignoreRequestArgs = "loginUser")
    public Result deleteTaskDefinitionByCode(@ApiIgnore @RequestAttribute(value = Constants.SESSION_USER) User loginUser,
                                             @ApiParam(name = "projectName", value = "PROJECT_NAME", required = true) @PathVariable String projectName,
                                             @RequestParam(value = "taskDefinitionCode") long taskDefinitionCode) {
        Map<String, Object> result = taskDefinitionService.deleteTaskDefinitionByCode(loginUser, projectName, taskDefinitionCode);
        return returnDataList(result);
    }

    /**
     * query detail of task definition by code
     *
     * @param loginUser login user
     * @param projectName project name
     * @param taskDefinitionCode the task definition code
     * @return task definition detail
     */
    @ApiOperation(value = "queryTaskDefinitionDetail", notes = "QUERY_TASK_DEFINITION_DETAIL_NOTES")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "taskDefinitionCode", value = "TASK_DEFINITION_CODE", required = true, dataType = "Long", example = "1")
    })
    @GetMapping(value = "/select-by-code")
    @ResponseStatus(HttpStatus.OK)
    @ApiException(QUERY_DETAIL_OF_TASK_DEFINITION_ERROR)
    @AccessLogAnnotation(ignoreRequestArgs = "loginUser")
    public Result queryTaskDefinitionDetail(@ApiIgnore @RequestAttribute(value = Constants.SESSION_USER) User loginUser,
                                            @ApiParam(name = "projectName", value = "PROJECT_NAME", required = true) @PathVariable String projectName,
                                            @RequestParam(value = "taskDefinitionCode") long taskDefinitionCode) {
        Map<String, Object> result = taskDefinitionService.queryTaskDefinitionDetail(loginUser, projectName, taskDefinitionCode);
        return returnDataList(result);
    }

    /**
     * query task definition list paging
     *
     * @param loginUser login user
     * @param projectName project name
     * @param searchVal search value
     * @param pageNo page number
     * @param pageSize page size
     * @param userId user id
     * @return task definition page
     */
    @ApiOperation(value = "queryTaskDefinitionListPaging", notes = "QUERY_TASK_DEFINITION_LIST_PAGING_NOTES")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNo", value = "PAGE_NO", required = true, dataType = "Int", example = "100"),
            @ApiImplicitParam(name = "searchVal", value = "SEARCH_VAL", required = false, type = "String"),
            @ApiImplicitParam(name = "userId", value = "USER_ID", required = false, dataType = "Int", example = "100"),
            @ApiImplicitParam(name = "pageSize", value = "PAGE_SIZE", required = true, dataType = "Int", example = "100")
    })
    @GetMapping(value = "/list-paging")
    @ResponseStatus(HttpStatus.OK)
    @ApiException(QUERY_TASK_DEFINITION_LIST_PAGING_ERROR)
    @AccessLogAnnotation(ignoreRequestArgs = "loginUser")
    public Result queryTaskDefinitionListPaging(@ApiIgnore @RequestAttribute(value = Constants.SESSION_USER) User loginUser,
                                                @ApiParam(name = "projectName", value = "PROJECT_NAME", required = true) @PathVariable String projectName,
                                                @RequestParam("pageNo") Integer pageNo,
                                                @RequestParam(value = "searchVal", required = false) String searchVal,
                                                @RequestParam(value = "userId", required = false, defaultValue = "0") Integer userId,
                                                @RequestParam("pageSize") Integer pageSize) {
        Map<String, Object> result = checkPageParams(pageNo, pageSize);
        if (result.get(Constants.STATUS) != Status.SUCCESS) {
            return returnDataListPaging(result);
        }
        searchVal = ParameterUtils.handleEscapes(searchVal);
        result = taskDefinitionService.queryTaskDefinitionListPaging(loginUser, projectName, searchVal, pageNo, pageSize, userId);
        return returnDataListPaging(result);
    }
}