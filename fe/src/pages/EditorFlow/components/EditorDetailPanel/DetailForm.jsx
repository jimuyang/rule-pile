import { Card, Form, Input, Select } from 'antd';
import React, { Fragment } from 'react';
import { withPropsAPI } from 'gg-editor';

import StartNodeDetail from './form/StartNodeDetail';
import LogicNodeDetail from './form/LogicNodeDetail';
import DecisionNodeDetail from './form/DecisionNodeDetail';
import EndNodeDetail from './form/EndNodeDetail';

const upperFirst = str => str.toLowerCase().replace(/( |^)[a-z]/g, l => l.toUpperCase());

const { Item } = Form;
const { Option } = Select;
const inlineFormItemLayout = {
  labelCol: {
    sm: {
      span: 6,
    },
  },
  wrapperCol: {
    sm: {
      span: 18,
    },
  },
};

class DetailForm extends React.Component {
  get item() {
    const { propsAPI } = this.props;
    return propsAPI.getSelected()[0];
  }

  handleSubmit = e => {
    if (e && e.preventDefault) {
      e.preventDefault();
    }

    const { form, propsAPI } = this.props;
    const { getSelected, executeCommand, update } = propsAPI;
    setTimeout(() => {
      form.validateFieldsAndScroll((err, values) => {
        if (err) {
          return;
        }

        const item = getSelected()[0];

        if (!item) {
          return;
        }

        executeCommand(() => {
          update(item, { ...values });
        });
      });
    }, 0);
  };

  renderEdgeShapeSelect = () => (
    <Select onChange={this.handleSubmit}>
      <Option value="flow-smooth">Smooth</Option>
      <Option value="flow-polyline">Polyline</Option>
      <Option value="flow-polyline-round">Polyline Round</Option>
    </Select>
  );

  renderNodeDetail = () => {
    // const { form } = this.props;
    // const { label } = this.item.getModel();
    // return (
    //   <Item label="Label" {...inlineFormItemLayout}>
    //     {form.getFieldDecorator('label', {
    //       initialValue: label,
    //     })(<Input onBlur={this.handleSubmit} />)}
    //   </Item>
    // );
    const { category } = this.item.getModel();
    // console.log(category);
    // debugger
    switch (category) {
      case 'start':
        return <StartNodeDetail />;
      case 'logic':
        return <LogicNodeDetail />;
      case 'end':
        return <EndNodeDetail />;
      default:
        return <DecisionNodeDetail />;
    }
  };

  renderEdgeDetail = () => {
    const { form } = this.props;
    const { label = '', valve = 'true' } = this.item.getModel();
    return (
      <Fragment>
        <Item label="Label" {...inlineFormItemLayout}>
          {form.getFieldDecorator('label', {
            initialValue: label,
          })(<Input onBlur={this.handleSubmit} />)}
        </Item>
        {/* <Item label="Shape" {...inlineFormItemLayout}>
          {form.getFieldDecorator('shape', {
            initialValue: shape,
          })(this.renderEdgeShapeSelect())}
        </Item> */}
        <Item label="valve" {...inlineFormItemLayout}>
          {form.getFieldDecorator('valve', {
            initialValue: valve,
          })(<Input onBlur={this.handleSubmit} />)}
        </Item>
      </Fragment>
    );
  };

  renderGroupDetail = () => {
    const { form } = this.props;
    const { label = '新建分组' } = this.item.getModel();
    return (
      <Item label="Label" {...inlineFormItemLayout}>
        {form.getFieldDecorator('label', {
          initialValue: label,
        })(<Input onBlur={this.handleSubmit} />)}
      </Item>
    );
  };

  render() {
    const { type } = this.props;

    if (!this.item) {
      return null;
    }

    return (
      <Card type="inner" size="small" title={upperFirst(type)} bordered={false}>
        <Form onSubmit={this.handleSubmit}>
          {type === 'node' && this.renderNodeDetail()}
          {type === 'edge' && this.renderEdgeDetail()}
          {type === 'group' && this.renderGroupDetail()}
        </Form>
      </Card>
    );
  }
}

export default Form.create()(withPropsAPI(DetailForm));
