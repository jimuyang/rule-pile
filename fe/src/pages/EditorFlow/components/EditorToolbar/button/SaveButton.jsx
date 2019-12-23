import { withPropsAPI } from 'gg-editor';
import React from 'react';
import { Icon, Tooltip } from 'antd';

// import http from '@/utils/request';

const SaveButton = props => (
  <Tooltip placement="bottom" title="保存">
    <div className="x-command">
      <Icon
        type="save"
        onClick={() => {
          // const search = new URLSearchParams(window.location.hash.split('?')[1]);
          // const ruleId = search.get('ruleId');
          // console.log(props);
          const editor = props.propsAPI.save();
          // eslint-disable-next-line no-console
          console.log(editor);
          // http.post('/ecs.qc/rule/definition/save', {
          //     ruleId,
          //     editor: JSON.stringify(editor),
          // }).then(() => {
          //     message.success('保存成功！');
          // });
        }}
      ></Icon>
    </div>
  </Tooltip>
);

export default withPropsAPI(SaveButton);
